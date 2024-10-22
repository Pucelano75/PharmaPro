package pharmapro.carlosnava




import SendReportScreen
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
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
                // Permiso concedido: ejecutar acción, como mostrar una notificación
                showNotification(this, "Permiso concedido", "Las notificaciones están habilitadas.")
            } else {
                showNotification(
                    this,
                    "Permiso denegado",
                    "No podrás recibir notificaciones."
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)

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
        // Manejo del botón "volver"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Salir de la app completamente al presionar el botón "volver"
                finishAffinity() // Cierra la app y todas las actividades
            }
        })

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
            val nfcChannelName = "NFC Notification Channel"
            val nfcDescriptionText = "Canal para notificaciones de NFC"
            val nfcImportance = NotificationManager.IMPORTANCE_HIGH
            val nfcChannel = NotificationChannel("nfc_channel", nfcChannelName, nfcImportance).apply {
                description = nfcDescriptionText
            }

            // Canal para recordatorios de medicación
            val medicationChannelName = "Medication Reminder Channel"
            val medicationDescription = "Canal para recordatorios de medicación"
            val medicationImportance = NotificationManager.IMPORTANCE_HIGH
            val medicationChannel = NotificationChannel("medication_channel", medicationChannelName, medicationImportance).apply {
                description = medicationDescription
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(nfcChannel)
            notificationManager.createNotificationChannel(medicationChannel)
        }
    }


    // Mostrar notificación
    private fun showNotification(context: Context, title: String, message: String, channelId: String = "medication_channel") {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo) // Cambia esto al icono que desees
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build()) // Usar un ID único
        } else {
            Toast.makeText(context, "Permiso para mostrar notificaciones no concedido.", Toast.LENGTH_SHORT).show()
        }
    }
    @Composable
    fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("registerMedication") { RegisterMedicationScreen(navController) }
            composable("records") { RecordsScreen(navController) }
            composable("programming") { ScheduleScreen(navController) }
            composable("send_report") { SendReportScreen(navController) }


        }
    }
}






