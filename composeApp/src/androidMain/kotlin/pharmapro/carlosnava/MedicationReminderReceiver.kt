package pharmapro.carlosnava

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Obtener el nombre del medicamento de los extras del intent
        val medicationName = intent.getStringExtra("medicationName") ?: "Medicamento desconocido"

        // Crear la notificación
        val notification = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(R.drawable.logo) // Cambia esto por tu icono
            .setContentTitle("Recordatorio de medicación")
            .setContentText("Es hora de tomar tu medicación: $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Si no se han concedido permisos, no se muestra la notificación
            return
        }
        notificationManager.notify(1, notification)
    }
}



