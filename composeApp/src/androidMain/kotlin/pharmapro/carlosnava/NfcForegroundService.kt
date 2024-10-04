package pharmapro.carlosnava

import android.app.Service
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.IBinder
import android.util.Log

class NfcForegroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NfcForegroundService", "Servicio NFC iniciado")

        // Verificar si el intent es para una etiqueta NFC
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                // Manejar la etiqueta NFC aquí
                val tagId = tag.id.joinToString("") { String.format("%02X", it) }
                Log.d("NfcForegroundService", "Tag ID: $tagId")
            }
        }

        // Si se quiere que el servicio continúe ejecutándose, usa START_STICKY
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // No se utiliza para un servicio en primer plano
    }
}
