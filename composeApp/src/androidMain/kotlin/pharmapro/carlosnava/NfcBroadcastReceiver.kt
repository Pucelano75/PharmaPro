import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log

class NfcBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NfcBroadcastReceiver", "NFC Intent Received: $intent")

        // Comprobar si el intent es para una etiqueta NFC
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent?.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            // Manejar el tag aquí...
            if (tag != null) {
                // Obtener la información de la etiqueta
                val tagId = tag.id.joinToString("") { String.format("%02X", it) }
                val technologies = tag.techList.toList()
                val ndefFormatable = tag.getTechList().contains("android.nfc.ndef.NdefFormatable")
                val ndef = tag.getTechList().contains("android.nfc.ndef.Ndef")

                Log.d("NfcBroadcastReceiver", "Tag ID: $tagId")
                Log.d("NfcBroadcastReceiver", "Supported Technologies: $technologies")
                Log.d("NfcBroadcastReceiver", "Is NDEF Format: $ndefFormatable")
                Log.d("NfcBroadcastReceiver", "Is NDEF: $ndef")
            }
        }
    }
}
