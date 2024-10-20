import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import pharmapro.carlosnava.SendReportWorker

@Composable
fun SendReportScreen(navController: NavController) {
    Button(onClick = {
        // Iniciar el Worker
        val sendReportWorkRequest = OneTimeWorkRequestBuilder<SendReportWorker>().build()
        var enqueue = WorkManager.getInstance(LocalContext.current).enqueue(sendReportWorkRequest)
    }) {
        Text("Enviar Informe")
    }
}

