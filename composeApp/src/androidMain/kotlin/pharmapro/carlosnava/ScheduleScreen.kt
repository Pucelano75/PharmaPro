package pharmapro.carlosnava

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Calendar

@Composable
fun ScheduleScreen(navController: NavController) {
    // Variables para almacenar los detalles de la medicación
    var medicationName by remember { mutableStateOf("") }
    var pauta by remember { mutableStateOf<Int?>(null) }
    var dias by remember { mutableStateOf<Int?>(null) }
    var horaInicio by remember { mutableStateOf("08:00") }
    var retardoAviso by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current // Obtener el contexto aquí

    // Cargar datos guardados al iniciar la pantalla
    LaunchedEffect(Unit) {
        val medicationDetails = loadMedicationDetails(context)
        medicationName = medicationDetails["medicacionNombre"] as String
        pauta = medicationDetails["pauta"] as Int? ?: null
        dias = medicationDetails["dias"] as Int? ?: null
        horaInicio = medicationDetails["horaInicio"] as String
        retardoAviso = medicationDetails["retardoAviso"] as Int? ?: null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la pantalla
        Text("Programación de Recordatorios", style = MaterialTheme.typography.titleLarge)

        TextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Nombre de la medicación") },
            placeholder = { Text("Introduce el nombre de la medicación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = pauta?.toString() ?: "",
            onValueChange = { pauta = it.toIntOrNull() },
            label = { Text("Pauta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = dias?.toString() ?: "",
            onValueChange = { dias = it.toIntOrNull() },
            label = { Text("Días") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = horaInicio,
            onValueChange = { horaInicio = it },
            label = { Text("Hora de inicio (HH:MM)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = retardoAviso?.toString() ?: "",
            onValueChange = { retardoAviso = it.toIntOrNull() },
            label = { Text("Retardo de aviso (minutos)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Guardar los detalles de la medicación
                saveMedicationDetails(
                    context = context,
                    medicacionNombre = medicationName,
                    pauta = pauta ?: 1,
                    dias = dias ?: 1,
                    horaInicio = horaInicio,
                    retardoAviso = retardoAviso ?: 0
                )

                // Programar los recordatorios
                scheduleMedicationReminders(
                    pauta = pauta ?: 1,
                    dias = dias ?: 1,
                    horaInicio = horaInicio,
                    retardoAviso = retardoAviso ?: 0,
                    medicacionNombre = medicationName,
                    context = context
                )

                // Navegar a otra pantalla si es necesario
                navController.navigate("records")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Funciones para guardar y cargar datos de SharedPreferences
fun saveMedicationDetails(context: Context, medicacionNombre: String, pauta: Int, dias: Int, horaInicio: String, retardoAviso: Int) {
    val sharedPreferences = context.getSharedPreferences("medication_preferences", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("medicacionNombre", medicacionNombre)
        putInt("pauta", pauta)
        putInt("dias", dias)
        putString("horaInicio", horaInicio)
        putInt("retardoAviso", retardoAviso)
        apply()
    }
}

fun loadMedicationDetails(context: Context): Map<String, Any> {
    val sharedPreferences = context.getSharedPreferences("medication_preferences", Context.MODE_PRIVATE)
    return mapOf(
        "medicacionNombre" to (sharedPreferences.getString("medicacionNombre", "") ?: ""),
        "pauta" to sharedPreferences.getInt("pauta", 0),
        "dias" to sharedPreferences.getInt("dias", 0),
        "horaInicio" to (sharedPreferences.getString("horaInicio", "") ?: ""),
        "retardoAviso" to sharedPreferences.getInt("retardoAviso", 0)
    )
}

// Función para programar recordatorios de medicación
fun scheduleMedicationReminders(
    pauta: Int,
    dias: Int,
    horaInicio: String,
    retardoAviso: Int,
    medicacionNombre: String,
    context: Context
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
        putExtra("medicationName", medicacionNombre)
    }

    val calendar = Calendar.getInstance()
    val (hour, minute) = horaInicio.split(":").map { it.toInt() }
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)

    for (day in 0 until dias) {
        calendar.add(Calendar.DAY_OF_YEAR, day * pauta)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            day,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis - (retardoAviso * 60 * 1000), // Restar el retardo
            AlarmManager.INTERVAL_DAY * pauta,
            pendingIntent
        )
        calendar.add(Calendar.DAY_OF_YEAR, -day * pauta) // Resetear el día para la próxima iteración
    }
}














