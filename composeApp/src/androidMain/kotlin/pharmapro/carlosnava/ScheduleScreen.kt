package pharmapro.carlosnava

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

@Composable
fun ScheduleScreen(navController: NavController) {
    // Variables para almacenar los detalles de la medicación
    var medicationName by remember { mutableStateOf("") }
    var pauta by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("08:00") }
    var retardoAviso by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Estado para manejar el TimePicker
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            horaInicio = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Cargar los recordatorios guardados en SharedPreferences
    var medicationReminders by remember {
        mutableStateOf(loadMedicationReminders(context).toMutableList())
    }

    // Función para eliminar recordatorio
    fun removeReminder(reminder: MedicationReminder) {
        medicationReminders = medicationReminders.filter { it != reminder }.toMutableList()
        saveMedicationReminders(context, medicationReminders) // Guardar cambios en SharedPreferences
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("Programación de Recordatorios", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

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
                    keyboardController?.hide()
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = pauta.toString(),
            onValueChange = { pauta = it },
            label = { Text("Pauta") },
            placeholder = { Text("Cantidad de veces al día") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = dias.toString(),
            onValueChange = { dias = it },
            label = { Text("Días") },
            placeholder = { Text("Número de días") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Text(text = "Seleccionar hora de inicio: $horaInicio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = retardoAviso.toString(),
            onValueChange = { retardoAviso = it },
            label = { Text("Retardo de aviso (minutos)") },
            placeholder = { Text("Ej. 10 minutos de retardo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Guardar el recordatorio en la lista
                val newReminder = MedicationReminder(
                    medicacionNombre = medicationName,
                    pauta = pauta.toIntOrNull() ?: 1,
                    dias = dias.toIntOrNull() ?: 1,
                    horaInicio = horaInicio,
                    retardoAviso = retardoAviso.toIntOrNull() ?: 0
                )

                // Programar recordatorio
                programarRecordatorioCompleto(context, newReminder)

                medicationReminders.add(newReminder)
                saveMedicationReminders(context, medicationReminders) // Guardar en SharedPreferences

                // Limpiar los campos
                medicationName = ""
                pauta = ""
                dias = ""
                horaInicio = "08:00"
                retardoAviso = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el resumen de los recordatorios programados
        Text("Recordatorios activos:", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        medicationReminders.forEach { reminder ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { removeReminder(reminder) }
            ) {
                Text(
                    text = "Medicamento: ${reminder.medicacionNombre} - Pauta: ${reminder.pauta} - Días: ${reminder.dias} - Hora: ${reminder.horaInicio} - Retardo: ${reminder.retardoAviso} minutos",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Clase para representar los recordatorios de medicación
data class MedicationReminder(
    val medicacionNombre: String,
    val pauta: Int,
    val dias: Int,
    val horaInicio: String,
    val retardoAviso: Int
)

// Funciones para guardar y cargar datos de SharedPreferences usando Gson
fun saveMedicationReminders(context: Context, reminders: List<MedicationReminder>) {
    val sharedPreferences = context.getSharedPreferences("medication_preferences", Context.MODE_PRIVATE)
    val gson = Gson()
    val jsonReminders = gson.toJson(reminders)

    with(sharedPreferences.edit()) {
        putString("medicationReminders", jsonReminders)
        apply()
    }
}

fun loadMedicationReminders(context: Context): List<MedicationReminder> {
    val sharedPreferences = context.getSharedPreferences("medication_preferences", Context.MODE_PRIVATE)
    val gson = Gson()
    val jsonReminders = sharedPreferences.getString("medicationReminders", null)

    return if (jsonReminders != null) {
        val type = object : TypeToken<List<MedicationReminder>>() {}.type
        gson.fromJson(jsonReminders, type)
    } else {
        emptyList()
    }
}

fun programarRecordatorioCompleto(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Verificar si la versión de Android es 12 o superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Para Android 12 (API 31) y superiores
        if (alarmManager.canScheduleExactAlarms()) {
            programarAlarmas(context, reminder)
        } else {
            // Informar al usuario que necesita activar el permiso
            redirigirAPermisosExactos(context)
        }
    } else {
        // Para Android 11 (API 30) y versiones inferiores, no es necesario este permiso
        programarAlarmas(context, reminder)
    }
}

fun programarAlarmas(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    for (day in 0 until reminder.dias) {
        for (i in 0 until reminder.pauta) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, day)

            val (startHour, startMinute) = reminder.horaInicio.split(":").map { it.toInt() }
            calendar.set(Calendar.HOUR_OF_DAY, startHour)
            calendar.set(Calendar.MINUTE, startMinute)

            calendar.add(Calendar.MINUTE, reminder.retardoAviso + i * (24 / reminder.pauta))

            val intent = Intent(context, MedicationReminderReceiver::class.java).apply {
                putExtra("medicationName", reminder.medicacionNombre)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.hashCode() + day * 100 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                Toast.makeText(context, "No se pudo programar una alarma exacta. Revisa los permisos.", Toast.LENGTH_LONG).show()
                // Redirigir a la configuración si falla
                redirigirAPermisosExactos(context)
            }
        }
    }
}

// Función para redirigir a la configuración de permisos de alarmas exactas (Solo para Android 12+)
fun redirigirAPermisosExactos(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        context.startActivity(intent)
    }
}





















