package pharmapro.carlosnava

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medicationName") ?: "Medicamento desconocido"
        Log.d("MedicationReminderReceiver", "Recordatorio recibido para: $medicationName")
        val notification = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Recordatorio de medicación")
            .setContentText("Es hora de tomar tu medicación: $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        notificationManager.notify(System.currentTimeMillis().toInt(), notification) // Usa un ID único
    }
}





