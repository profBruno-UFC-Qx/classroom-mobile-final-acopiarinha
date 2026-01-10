package com.example.notificationboleto.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationboleto.data.local.database.AppDatabase
import com.example.notificationboleto.data.local.entity.BoletoEntity
import com.example.notificationboleto.data.repository.BoletoRepository
import com.example.notificationboleto.notifications.BoletoNotificationReceiver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BoletoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BoletoRepository by lazy {
        val dao = AppDatabase.getInstance(application).boletoDao()
        BoletoRepository(dao)
    }

    private val alarmManager =
        application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val boletos = repository.getBoletos()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun addBoleto(nome: String, valor: String, vencimento: String, descricao: String) {
        val boleto = BoletoEntity(
            id = UUID.randomUUID().toString(),
            nome = nome,
            valor = valor,
            vencimento = vencimento,
            descricao = descricao
        )

        viewModelScope.launch {
            repository.addBoleto(boleto)
            agendarNotificacao(boleto)
        }
    }

    fun updateBoleto(id: String, nome: String, valor: String, vencimento: String, descricao: String) {
        val boleto = BoletoEntity(id, nome, valor, vencimento, descricao)

        viewModelScope.launch {
            repository.updateBoleto(boleto)
            agendarNotificacao(boleto)
        }
    }

    fun deleteBoleto(boleto: BoletoEntity) {
        viewModelScope.launch {
            cancelarNotificacao(boleto.id)
            repository.deleteBoleto(boleto)
        }
    }

    private fun agendarNotificacao(boleto: BoletoEntity) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val data = sdf.parse(boleto.vencimento) ?: return

            val calendar = Calendar.getInstance().apply {
                time = data
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val intent = Intent(getApplication(), BoletoNotificationReceiver::class.java).apply {
                putExtra("titulo", "Boleto vence amanhã")
                putExtra("descricao", "${boleto.nome} - ${boleto.valor}")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                getApplication(),
                boleto.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelarNotificacao(id: String) {
        val intent = Intent(getApplication(), BoletoNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
