package pharmapro.carlosnava

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Estado para manejar el TimePicker
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            // Actualizar la hora cuando se selecciona
            horaInicio = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

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
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        // Título de la pantalla
        Text("Programación de Recordatorios", style = MaterialTheme.typography.titleLarge)


        Spacer(modifier = Modifier.height(8.dp))

        // Campo de nombre de la medicación
        TextField(
            value = medicationName,
            onValueChange = { medicationName = it },
            label = { Text("Nombre de la medicación") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusRequester.requestFocus()
                    keyboardController?.hide() // Ocultar teclado al finalizar
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de pauta
        TextField(
            value = pauta.toString(),
            onValueChange = { pauta = it.toIntOrNull() ?: 1 },
            label = { Text("Pauta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de días
        TextField(
            value = dias.toString(),
            onValueChange = { dias = it.toIntOrNull() ?: 1 },
            label = { Text("Días") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para seleccionar la hora de inicio con el TimePicker
        Button(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Seleccionar hora de inicio: $horaInicio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de retardo de aviso
        TextField(
            value = retardoAviso.toString(),
            onValueChange = { retardoAviso = it.toIntOrNull() ?: 0 },
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

fun scheduleMedicationReminders(
    pauta: Int,
    dias: Int,
    horaInicio: String,
    retardoAviso: Int,
    medicacionNombre: String,
    context: Context
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Verificar si el dispositivo puede programar alarmas exactas
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            // Informar al usuario o manejar el caso cuando no se pueden programar alarmas exactas
            Toast.makeText(context, "No se pueden programar alarmas exactas", Toast.LENGTH_LONG).show()
            return // Salir de la función si no se pueden programar alarmas exactas
        }
    }

    val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
        putExtra("medicationName", medicacionNombre)
    }

    val calendar = Calendar.getInstance()
    val (hour, minute) = horaInicio.split(":").map { it.toInt() }
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)

    try {
        for (day in 0 until dias) {
            for (dose in 0 until pauta) {
                // Establece la hora de cada dosis del día
                calendar.add(Calendar.HOUR_OF_DAY, dose * (24 / pauta))

                // Añadir el retardo en minutos al tiempo programado
                calendar.add(Calendar.MINUTE, retardoAviso)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    day * 1000 + dose, // ID único para cada alarma
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Usar setExactAndAllowWhileIdle para alarmas exactas
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, //se añade el tiempo de retardo a la hora programada
                    pendingIntent
                )

                // Resetear la hora para la próxima dosis
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
            }

            // Adelantar el día para las siguientes dosis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    } catch (e: SecurityException) {
        // Manejar el caso cuando no se pueden programar alarmas exactas debido a restricciones de permisos
        Toast.makeText(context, "No se pueden programar alarmas exactas debido a restricciones de permisos.", Toast.LENGTH_LONG).show()
    }
}















