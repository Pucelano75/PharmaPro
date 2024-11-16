package pharmapro.carlosnava

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medicationName") ?: "Medicación"

        // Reiniciar el estado de la notificación en SharedPreferences
        val prefs = context.getSharedPreferences("MedReminderPrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("isNotificationPressed", false).apply()

        // Crear la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Recordatorios de Medicación",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Crear el intent para abrir la app al presionar la notificación
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("medication_reminder", true)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Recordatorio de Medicación")
            .setContentText("Es hora de tomar tu $medicationName")
            .setSmallIcon(R.drawable.logotipo) // Reemplaza con tu icono
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        notificationManager.notify(1001, notification)

        // Iniciar el WorkManager para los recordatorios periódicos cada 15 minutos
        scheduleRecurringReminder(context, medicationName)
    }

    // Función para configurar el Worker con un recordatorio recurrente cada 15 minutos
    private fun scheduleRecurringReminder(context: Context, medicationName: String) {
        val data = Data.Builder()
            .putString("medicationName", medicationName)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<MedicationReminderWorker>(15, java.util.concurrent.TimeUnit.MINUTES)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "MedicationReminderWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}









