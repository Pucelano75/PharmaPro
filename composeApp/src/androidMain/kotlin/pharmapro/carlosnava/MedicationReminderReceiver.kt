package pharmapro.carlosnava

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medicationName") ?: "Medicación"

        // Crear la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Medicación",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Crear el intent que se lanzará al hacer clic en la notificación
        val notificationIntent = Intent(context, MainActivity::class.java) // Cambia MainActivity por tu actividad principal
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
        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }
}








