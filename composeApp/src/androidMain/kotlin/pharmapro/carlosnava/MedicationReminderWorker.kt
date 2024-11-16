package pharmapro.carlosnava

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MedicationReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val medicationName = inputData.getString("medicationName") ?: "Medicación"

        // Obtener el estado actual de la notificación
        val prefs = applicationContext.getSharedPreferences("MedReminderPrefs", Context.MODE_PRIVATE)
        val isNotificationPressed = prefs.getBoolean("isNotificationPressed", false)

        if (!isNotificationPressed) {
            // Crear la notificación
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

            // Crear el intent que se lanzará al hacer clic en la notificación
            val notificationIntent = Intent(applicationContext, MainActivity::class.java).apply {
                putExtra("medication_reminder", true)
            }
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            // Construir la notificación
            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle("Recordatorio de Medicación")
                .setContentText("Es hora de tomar tu $medicationName")
                .setSmallIcon(R.drawable.logotipo) // Reemplaza con tu icono
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            // Mostrar la notificación
            notificationManager.notify(1001, notification)
        }

        return Result.success()
    }
}
