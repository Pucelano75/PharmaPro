package pharmapro.carlosnava

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pharmapro.carlosnava.ui.theme.PharmaProTheme

class MainActivity : ComponentActivity() {
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permiso concedido
            } else {
                // Permiso denegado
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Asegúrate de que esta línea esté intacta
        createNotificationChannel(this) // Crear el canal de notificación

        // Solicitar permisos para notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permiso ya concedido
                }
                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        setContent {
            PharmaProTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Navigation() // Llama a la función de navegación
                }
            }
        }
    }

    // Crear canal de notificaciones
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NFC Notification Channel"
            val descriptionText = "Canal para notificaciones de NFC"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("nfc_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Mostrar notificación
    fun showNotification(context: Context, title: String, message: String) {
        val builder = NotificationCompat.Builder(context, "nfc_channel")
            .setSmallIcon(R.drawable.logo) // Cambia esto al icono que desees
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("main") { MainScreen(navController) }
        //composable("register") { RegisterScreen(navController) }
        //composable("login") { LoginScreen(navController = navController) }
        composable("home") { HomeScreen(navController) }
        composable("registerMedication") { RegisterMedicationScreen(navController) }
        composable("records") { RecordsScreen(navController)  }
    }
}







