package com.example.notificationboleto.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Debug
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.notificationboleto.BuildConfig
import com.example.notificationboleto.data.local.database.AppDatabase
import com.example.notificationboleto.data.local.entity.BoletoEntity
import com.example.notificationboleto.data.repository.BoletoRepository
import com.example.notificationboleto.notifications.BoletoNotificationReceiver
import com.example.notificationboleto.notifications.WeeklyBoletoWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

    init {
        setupWeeklyNotification()

        viewModelScope.launch {
            delay(3000) // Aguarda o debugger anexar completamente
            if (BuildConfig.DEBUG && Debug.isDebuggerConnected()) {
                startDebugNotificationTest()
            }
        }
    }

    private fun startDebugNotificationTest() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            while (true) {

                if (!Debug.isDebuggerConnected()) break

                val listaBoletos = repository.getAllBoletos()
                val hoje = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.time

                val amanha = Calendar.getInstance().apply {
                    time = hoje; add(Calendar.DAY_OF_YEAR, 1)
                }.time

                var vencidosCount = 0

                listaBoletos.forEach { boleto ->
                    try {
                        val dataVenc = sdf.parse(boleto.vencimento) ?: return@forEach

                        when {
                            sdf.format(dataVenc) == sdf.format(hoje) -> {
                                triggerInstantNotification("TESTE: Vence Hoje", "${boleto.nome} - ${boleto.valor}", 880)
                            }
                            sdf.format(dataVenc) == sdf.format(amanha) -> {
                                triggerInstantNotification("TESTE: Vence Amanhã", "${boleto.nome} - ${boleto.valor}", 881)
                            }
                            dataVenc.before(hoje) -> {
                                vencidosCount++
                            }
                        }
                    } catch (e: Exception) { }
                }

                if (vencidosCount > 0) {
                    triggerWeeklyDebugNotification(vencidosCount)
                }

                delay(30_000) // Intervalo de teste solicitado
            }
        }
    }

    private fun triggerInstantNotification(titulo: String, descricao: String, id: Int) {
        val intent = Intent(getApplication(), BoletoNotificationReceiver::class.java).apply {
            putExtra("titulo", titulo)
            putExtra("descricao", descricao)
            putExtra("id", id)
        }
        getApplication<Application>().sendBroadcast(intent)
    }

    private fun triggerWeeklyDebugNotification(total: Int) {
        val workManager = WorkManager.getInstance(getApplication())
        val debugRequest = OneTimeWorkRequestBuilder<WeeklyBoletoWorker>()
            .setInputData(workDataOf("is_debug" to true, "debug_total" to total))
            .build()
        workManager.enqueue(debugRequest)
    }

    private fun setupWeeklyNotification() {
        val weeklyRequest = PeriodicWorkRequestBuilder<WeeklyBoletoWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            "weekly_boleto_summary",
            ExistingPeriodicWorkPolicy.KEEP,
            weeklyRequest
        )
    }

    fun addBoleto(nome: String, valor: String, vencimento: String, descricao: String) {
        val boleto = BoletoEntity(UUID.randomUUID().toString(), nome, valor, vencimento, descricao)
        viewModelScope.launch {
            repository.addBoleto(boleto)
            agendarNotificacoes(boleto)
        }
    }

    fun updateBoleto(id: String, nome: String, valor: String, vencimento: String, descricao: String) {
        val boleto = BoletoEntity(id, nome, valor, vencimento, descricao)
        viewModelScope.launch {
            repository.updateBoleto(boleto)
            agendarNotificacoes(boleto)
        }
    }

    fun deleteBoleto(boleto: BoletoEntity) {
        viewModelScope.launch {
            cancelarNotificacoes(boleto.id)
            repository.deleteBoleto(boleto)
        }
    }

    private fun agendarNotificacoes(boleto: BoletoEntity) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val data = sdf.parse(boleto.vencimento) ?: return

            val calAmanha = Calendar.getInstance().apply {
                time = data; add(Calendar.DAY_OF_YEAR, -1); set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }
            agendarAlarm(calAmanha.timeInMillis, boleto.id.hashCode(), "Boleto vence amanhã", "${boleto.nome} - ${boleto.valor}")

            val calHoje = Calendar.getInstance().apply {
                time = data; set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }
            agendarAlarm(calHoje.timeInMillis, boleto.id.hashCode() + 1, "Boleto vence hoje", "${boleto.nome} - ${boleto.valor}")
        } catch (e: Exception) { }
    }

    private fun agendarAlarm(timeInMillis: Long, requestCode: Int, titulo: String, descricao: String) {
        if (timeInMillis <= System.currentTimeMillis()) return
        val intent = Intent(getApplication(), BoletoNotificationReceiver::class.java).apply {
            putExtra("titulo", titulo); putExtra("descricao", descricao); putExtra("id", requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(getApplication(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    private fun cancelarNotificacoes(id: String) {
        val intent = Intent(getApplication(), BoletoNotificationReceiver::class.java)
        val piAmanha = PendingIntent.getBroadcast(getApplication(), id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(piAmanha)
        val piHoje = PendingIntent.getBroadcast(getApplication(), id.hashCode() + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(piHoje)
    }
}
