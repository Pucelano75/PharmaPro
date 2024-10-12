package pharmapro.carlosnava

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

@Composable
fun ScheduleScreen(navController: NavController) {
    // Estados para almacenar los valores de los campos
    val (pauta, setPauta) = remember { mutableStateOf("") }
    val (dias, setDias) = remember { mutableStateOf("") }
    val (horaInicio, setHoraInicio) = remember { mutableStateOf("") }
    val (retardoAviso, setRetardoAviso) = remember { mutableStateOf("") }

    // Diseño de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Programación de Medicación",
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo para seleccionar la pauta (cada cuántas horas)
        OutlinedTextField(
            value = pauta,
            onValueChange = setPauta,
            label = { Text("Pauta (horas)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Campo para ingresar el número de días
        OutlinedTextField(
            value = dias,
            onValueChange = setDias,
            label = { Text("Días") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Campo para ingresar la hora de inicio (formato HH:mm)
        OutlinedTextField(
            value = horaInicio,
            onValueChange = setHoraInicio,
            label = { Text("Hora de Inicio (HH:mm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Campo para ingresar el retardo de aviso (minutos)
        OutlinedTextField(
            value = retardoAviso,
            onValueChange = setRetardoAviso,
            label = { Text("Retardo de Aviso (minutos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        // Botón para programar los recordatorios
        Button(
            onClick = {
                if (pauta.isNotEmpty() && dias.isNotEmpty() && horaInicio.isNotEmpty() && retardoAviso.isNotEmpty()) {
                    scheduleMedicationReminders(
                        pauta = pauta.toInt(),
                        dias = dias.toInt(),
                        horaInicio = horaInicio,
                        retardoAviso = retardoAviso.toInt(),
                        context = navController.context
                    )
                    Toast.makeText(navController.context, "Recordatorios programados", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(navController.context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Programar")
        }
    }
}

// Función para programar los recordatorios de medicación
@SuppressLint("ScheduleExactAlarm")
fun scheduleMedicationReminders(
    pauta: Int,  // Horas entre tomas
    dias: Int,   // Número de días
    horaInicio: String,  // Hora en formato "HH:mm"
    retardoAviso: Int,  // Tiempo de retardo en minutos
    context: Context
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationIntent = Intent(context, MedicationReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Obtener la hora actual y la hora de inicio
    val (hours, minutes) = horaInicio.split(":").map { it.toInt() }
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, 0)
    }

    // Programar las notificaciones
    for (i in 0 until dias) {
        val triggerTime = calendar.timeInMillis + i * AlarmManager.INTERVAL_DAY + pauta * AlarmManager.INTERVAL_HOUR
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}
