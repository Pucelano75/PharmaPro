package pharmapro.carlosnava

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Aquí puedes realizar las acciones necesarias tras el reinicio del dispositivo
            Log.d("BootReceiver", "Dispositivo iniciado")
        }
    }
}
