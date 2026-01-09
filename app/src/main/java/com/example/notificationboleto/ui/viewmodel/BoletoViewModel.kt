package com.example.notificationboleto.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.notificationboleto.notifications.BoletoNotificationReceiver
import java.text.SimpleDateFormat
import java.util.*

data class BoletoUiModel(
    val id: String = UUID.randomUUID().toString(),
    val valor: String,
    val vencimento: String,
    val descricao: String
)

class BoletoViewModel(application: Application) : AndroidViewModel(application) {

    private val _boletos = mutableStateListOf<BoletoUiModel>()
    val boletos: List<BoletoUiModel> = _boletos

    fun addBoleto(valor: String, vencimento: String, descricao: String) {
        val novoBoleto = BoletoUiModel(
            valor = valor,
            vencimento = vencimento,
            descricao = descricao
        )
        _boletos.add(novoBoleto)
        agendarNotificacao(novoBoleto)
    }

    fun updateBoleto(id: String, valor: String, vencimento: String, descricao: String) {
        val index = _boletos.indexOfFirst { it.id == id }
        if (index != -1) {
            val boletoAtualizado = _boletos[index].copy(
                valor = valor,
                vencimento = vencimento,
                descricao = descricao
            )
            _boletos[index] = boletoAtualizado
            agendarNotificacao(boletoAtualizado)
        }
    }

    fun deleteBoleto(id: String) {
        cancelarNotificacao(id)
        _boletos.removeIf { it.id == id }
    }

    private fun agendarNotificacao(boleto: BoletoUiModel) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dataVencimento = sdf.parse(boleto.vencimento) ?: return
            
            val calendar = Calendar.getInstance()
            calendar.time = dataVencimento
            calendar.add(Calendar.DAY_OF_YEAR, -1) // Subtrai 1 dia
            calendar.set(Calendar.HOUR_OF_DAY, 9) // Notifica às 9h da manhã
            calendar.set(Calendar.MINUTE, 0)
            
            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val intent = Intent(getApplication(), BoletoNotificationReceiver::class.java).apply {
                putExtra("titulo", "Vencimento Amanhã!")
                putExtra("descricao", "Boleto de ${boleto.valor} vence amanhã.")
                putExtra("id", boleto.id)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                getApplication(),
                boleto.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
