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
import androidx.annotation.RequiresApi
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
import pharmapro.carlosnava.MedicationReminderReceiver
import java.util.Calendar

@Composable
fun ScheduleScreen(navController: NavController) {
    var medicationName by remember { mutableStateOf("") }
    var pauta by remember { mutableStateOf("") }
    var dias by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("08:00") }
    var retardoAviso by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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

    var medicationReminders by remember {
        mutableStateOf(loadMedicationReminders(context).toMutableList())
    }

    fun removeReminder(reminder: MedicationReminder) {
        cancelarAlarmas(context, reminder)
        medicationReminders = medicationReminders.filter { it != reminder }.toMutableList()
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
                val newReminder = MedicationReminder(
                    medicacionNombre = medicationName,
                    pauta = pauta.toIntOrNull() ?: 1,
                    dias = dias.toIntOrNull() ?: 1,
                    horaInicio = horaInicio,
                    retardoAviso = retardoAviso.toIntOrNull() ?: 0
                )

                programarRecordatorioCompleto(context, newReminder)

                medicationReminders.add(newReminder)
                saveMedicationReminders(context, medicationReminders)

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

data class MedicationReminder(
    val medicacionNombre: String,
    val pauta: Int,
    val dias: Int,
    val horaInicio: String,
    val retardoAviso: Int
)

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

fun cancelarAlarmas(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    for (day in 0 until reminder.dias) {
        for (i in 0 until reminder.pauta) {
            val intent = Intent(context, MedicationReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.hashCode() + day * 100 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }
}

fun programarRecordatorioCompleto(context: Context, reminder: MedicationReminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (alarmManager.canScheduleExactAlarms()) {
            programarAlarmas(context, alarmManager, reminder)
        } else {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }
    } else {
        programarAlarmas(context, alarmManager, reminder)
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun programarAlarmas(context: Context, alarmManager: AlarmManager, reminder: MedicationReminder) {
    val calendar = Calendar.getInstance()
    val (hora, minuto) = reminder.horaInicio.split(":").map { it.toInt() }

    calendar.set(Calendar.HOUR_OF_DAY, hora)
    calendar.set(Calendar.MINUTE, minuto)
    calendar.set(Calendar.SECOND, 0)

    if (calendar.timeInMillis <= System.currentTimeMillis()) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    for (day in 0 until reminder.dias) {
        val currentCalendar = calendar.clone() as Calendar
        currentCalendar.add(Calendar.DAY_OF_MONTH, day)

        for (i in 0 until reminder.pauta) {
            val intent = Intent(context, MedicationReminderReceiver::class.java)
            intent.putExtra("medicationName", reminder.medicacionNombre)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.hashCode() + day * 100 + i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                currentCalendar.timeInMillis,
                pendingIntent
            )

            Log.d("AlarmasProgramadas", "Alarma programada para: ${currentCalendar.time}")

            currentCalendar.add(Calendar.MINUTE, (24 * 60) / reminder.pauta)
        }
    }
}























