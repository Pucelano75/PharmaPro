package pharmapro.carlosnava

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MedicationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicationName = intent.getStringExtra("medicationName") ?: return  // Retornar si no hay nombre
        val alarmId = intent.getIntExtra("alarmId", -1)


        // Crear un canal de notificación si es necesario
        createNotificationChannel(context)

        // Intent para abrir la MainActivity
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent que se ejecutará cuando el usuario haga clic en la notificación
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "medication_channel")
            .setSmallIcon(R.drawable.logo) // Asegúrate de que este icono sea adecuado
            .setContentTitle("Recordatorio de medicación")
            .setContentText("Es hora de tomar tu medicación: $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Asigna el PendingIntent a la notificación
            .setAutoCancel(true) // Hace que la notificación se cierre al hacer clic en ella
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(alarmId, notification) // Usar un ID único
        }

        // Registrar que la medicación ha sido tomada para esta hora específica
        registerMedicationTaken(context, medicationName)

        // Cancelar la alarma una vez que se ha mostrado
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val cancelIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(cancelIntent)  // Cancelar la alarma programada
    }

    private fun registerMedicationTaken(context: Context, medicationName: String) {
        // Aquí puedes actualizar tus registros o guardar en SharedPreferences que la medicación fue tomada para esta toma
        Toast.makeText(context, "Has registrado la toma de $medicationName", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MedicationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)  // Cancelar solo la alarma específica
    }

    private fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "medication_channel"
            val channelName = "Recordatorios de Medicación"
            val channelDescription = "Canal para recordatorios de medicación"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}







