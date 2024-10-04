package pharmapro.carlosnava

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import pharmapro.carlosnava.NotificationUtils

class NfcBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NfcBroadcastReceiver", "NFC Intent Received: $intent")

        if (context == null) return

        // Comprobar si el intent es para una etiqueta NFC
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                val tagId = tag.id.joinToString("") { String.format("%02X", it) }
                Log.d("NfcBroadcastReceiver", "Tag ID: $tagId")

                // Guardar la toma de medicación en SharedPreferences
                val sharedPreferences: SharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val currentRecords = sharedPreferences.getString("medicationRecords", "") ?: ""
                val newRecord = "Medicación tomada - ID: $tagId - Fecha: ${System.currentTimeMillis()}\n\n"
                editor.putString("medicationRecords", currentRecords + newRecord)
                editor.apply()

                // Mostrar notificación de confirmación
                NotificationUtils.showNotification(context, "Toma registrada", "La toma de medicación ha sido registrada con éxito.")
            }
        }
    }
}


