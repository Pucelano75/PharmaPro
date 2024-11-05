package pharmapro.carlosnava

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.TimePicker
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
        // Primero, cancelar las alarmas programadas
        cancelarAlarmas(context, reminder)

        // Luego, eliminar el recordatorio de la lista
        medicationReminders = medicationReminders.filter { it != reminder }.toMutableList()

        // Guardar cambios en SharedPreferences
        saveMedicationReminders(context, medicationReminders)
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
            value = pauta,
            onValueChange = { pauta = it },
            label = { Text("Pauta") },
            placeholder = { Text("Cantidad de veces al día") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = dias,
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
            value = retardoAviso,
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

// Función para cancelar todas las alarmas asociadas a un recordatorio
fun cancelarAlarmas(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    for (day in 0 until reminder.dias) {
        for (i in 0 until reminder.pauta) {
            val intent = Intent(context, MedicationReminderReceiver::class.java)

            // Usar el mismo identificador único que al programar las alarmas
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.hashCode() + day * 100 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Cancelar la alarma
            alarmManager.cancel(pendingIntent)
        }
    }
}

fun programarRecordatorioCompleto(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Verificar si la versión de Android es 12 o superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Para Android 12 (API 31) y superiores
        if (alarmManager.canScheduleExactAlarms()) {
            programarAlarmas(context, alarmManager, reminder)
        } else {
            // Si no se pueden programar alarmas exactas, redirigir al usuario
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }
    } else {
        // Para versiones anteriores a Android 12
        programarAlarmas(context, alarmManager, reminder)
    }
}

fun programarAlarmas(context: Context, alarmManager: AlarmManager, reminder: MedicationReminder) {
    val calendar = Calendar.getInstance()
    val (hora, minuto) = reminder.horaInicio.split(":").map { it.toInt() }

    // Establecer la hora de inicio de la primera alarma
    calendar.set(Calendar.HOUR_OF_DAY, hora)
    calendar.set(Calendar.MINUTE, minuto)
    calendar.set(Calendar.SECOND, 0)

    // Si la hora de inicio ya pasó hoy, establece el inicio en el día siguiente
    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Calcular el intervalo de minutos entre cada alarma en función de la pauta
    val intervaloMinutos = (24 * 60) / reminder.pauta

    for (day in 0 until reminder.dias) {
        // Clona el calendario para cada día específico de alarma
        val currentCalendar = calendar.clone() as Calendar
        currentCalendar.add(Calendar.DAY_OF_MONTH, day)

        // Programar una alarma para cada intervalo basado en la pauta
        for (i in 0 until reminder.pauta) {
            val intent = Intent(context, MedicationReminderReceiver::class.java)
            intent.putExtra("medicationName", reminder.medicacionNombre)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.hashCode() + day * 100 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Programar la alarma
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                currentCalendar.timeInMillis,
                pendingIntent
            )

            // Log para verificar el tiempo de cada alarma
            Log.d("ProgramacionDeAlarmas", "Programando alarma para: ${currentCalendar.time}")

            // Incrementar el tiempo para la siguiente alarma en base al intervalo
            currentCalendar.add(Calendar.MINUTE, intervaloMinutos)
        }
    }
}























