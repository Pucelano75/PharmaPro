import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pharmapro.carlosnava.SendReportWorker

@Composable
fun SendReportScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    var isDailyReport by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enviar Informe por WhatsApp", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para introducir el número de teléfono
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Número de teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        // Instrucciones para el formato del número de teléfono
        Text(
            text = "Formato: +34 123 456 789", // Ejemplo para España
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opción para activar el envío diario
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enviar informe diariamente")
            Switch(
                checked = isDailyReport,
                onCheckedChange = { isDailyReport = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para enviar el informe
        Button(
            onClick = {
                if (phoneNumber.isNotEmpty()) {
                    // Configurar el WorkRequest para enviar el informe
                    val workRequest = OneTimeWorkRequestBuilder<SendReportWorker>()
                        .setInputData(workDataOf("PHONE_NUMBER" to phoneNumber))
                        .build()

                    WorkManager.getInstance(context).enqueue(workRequest)
                    Toast.makeText(context, "Informe enviado a WhatsApp.", Toast.LENGTH_SHORT).show()

                    // Programar el trabajo diario si está activado
                    if (isDailyReport) {
                        scheduleDailyReport(context, phoneNumber) // Necesitas implementar esta función
                    }
                } else {
                    Toast.makeText(context, "Por favor, ingrese un número de teléfono.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Informe")
        }
    }
}

// Función para programar el envío diario (implementación básica)
fun scheduleDailyReport(context: Context, phoneNumber: String) {
    // Aquí iría la lógica para programar el WorkManager para enviar el informe a las 23:59
    // Por ejemplo, utilizando PeriodicWorkRequest
}

@Preview(showBackground = true)
@Composable
fun SendReportScreenPreview() {
    SendReportScreen(navController = rememberNavController())
}





