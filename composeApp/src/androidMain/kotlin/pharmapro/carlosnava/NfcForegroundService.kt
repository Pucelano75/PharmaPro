package pharmapro.carlosnava

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class NfcForegroundService : Service() {
    private val CHANNEL_ID = "nfc_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NfcForegroundService", "Servicio NFC iniciado")

        // Crear el canal de notificaciones
        createNotificationChannel()

        // Crear la notificación
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio NFC")
            .setContentText("Escuchando etiquetas NFC...")
            .setSmallIcon(R.drawable.logo) // Cambia esto al icono que desees
            .build()

        // Iniciar el servicio en primer plano
        startForeground(1, notification)

        // Verificar si el intent es para una etiqueta NFC
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                // Manejar la etiqueta NFC aquí
                val tagId = tag.id.joinToString("") { String.format("%02X", it) }
                Log.d("NfcForegroundService", "Tag ID: $tagId")

                // Aquí puedes realizar el registro de la toma de medicación o lo que necesites
            }
        }

        // Si se quiere que el servicio continúe ejecutándose, usa START_STICKY
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NFC Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // No se utiliza para un servicio en primer plano
    }
}

