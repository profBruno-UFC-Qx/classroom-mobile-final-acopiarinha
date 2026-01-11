package com.example.notificationboleto.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.notificationboleto.data.local.database.AppDatabase
import com.example.notificationboleto.data.repository.BoletoRepository
import java.text.SimpleDateFormat
import java.util.*

class WeeklyBoletoWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val isDebug = inputData.getBoolean("is_debug", false)
        val debugTotal = inputData.getInt("debug_total", 0)

        if (isDebug) {
            enviarNotificacao(debugTotal, true)
            return Result.success()
        }

        val dao = AppDatabase.getInstance(applicationContext).boletoDao()
        val repository = BoletoRepository(dao)
        val boletos = repository.getAllBoletos()

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val hoje = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val boletosVencidos = boletos.count {
            try {
                val dataVencimento = sdf.parse(it.vencimento)
                dataVencimento != null && dataVencimento.before(hoje)
            } catch (e: Exception) {
                false
            }
        }

        if (boletosVencidos > 0) {
            enviarNotificacao(boletosVencidos)
        }

        return Result.success()
    }

    private fun enviarNotificacao(total: Int, isDebug: Boolean = false) {
        val context = applicationContext
        val channelId = "weekly_boleto_notifications"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Resumo Semanal de Boletos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val prefix = if (isDebug) "DEBUG: " else ""
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("${prefix}Resumo Semanal")
            .setContentText("Você possui $total boletos vencidos.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(if (isDebug) 998 else 999, notification)
    }
}
