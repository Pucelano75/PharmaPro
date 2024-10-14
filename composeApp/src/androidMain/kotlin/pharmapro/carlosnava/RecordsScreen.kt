package pharmapro.carlosnava

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import java.text.ParseException

@Composable
fun RecordsScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)

    // Recuperar registros guardados
    val medicationRecords = remember {
        mutableStateOf(
            sharedPreferences.getString("medicationRecords", "")?.split("\n\n")
                ?.filter { it.isNotEmpty() } ?: emptyList()
        )
    }
    val selectedRecords = remember { mutableStateOf(mutableSetOf<Int>()) }
    // Lógica para chequear recordatorios
    LaunchedEffect(Unit) {
        checkMedicationReminders(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registros de Medicación",
            style = MaterialTheme.typography.headlineMedium, // headlineMedium en lugar de h5
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de registros
        LazyColumn(
            modifier = Modifier.weight(1f) // para que la columna ocupe el espacio disponible
        ) {
            itemsIndexed(medicationRecords.value) { index, record ->
                val isSelected = selectedRecords.value.contains(index)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { isChecked ->
                            val updatedSelected = selectedRecords.value.toMutableSet()
                            if (isChecked) {
                                updatedSelected.add(index)
                            } else {
                                updatedSelected.remove(index)
                            }
                            selectedRecords.value = updatedSelected
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = record, modifier = Modifier.weight(1f))
                }
                Divider(color = Color.Gray, thickness = 1.dp) // Línea separadora entre registros
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el botón de eliminar si hay registros seleccionados
        if (selectedRecords.value.isNotEmpty()) {
            Button(
                onClick = {
                    // Eliminar registros seleccionados
                    medicationRecords.value = medicationRecords.value.filterIndexed { index, _ ->
                        !selectedRecords.value.contains(index)
                    }
                    // Guardar los registros actualizados en SharedPreferences
                    sharedPreferences.edit()
                        .putString("medicationRecords", medicationRecords.value.joinToString("\n\n"))
                        .apply()
                    selectedRecords.value.clear() // Limpiar la selección
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Eliminar Seleccionados")
            }
        }
    }
}
fun checkMedicationReminders(context: Context) {
    val sharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)

    // Recuperar registros guardados
    val medicationRecords = sharedPreferences.getString("medicationRecords", "")?.split("\n\n")
        ?.filter { it.isNotEmpty() } ?: emptyList()

    // Para cada medicación en los registros, verificar su recordatorio
    for (record in medicationRecords) {
        val medicationName = record.lines().find { it.startsWith("Medicamento:") }
            ?.removePrefix("Medicamento: ")?.trim()

        if (medicationName != null) {
            // Recuperar parámetros específicos para esta medicación
            val dosageFrequency = sharedPreferences.getInt("dosageFrequency_$medicationName", 1)
            val dosageDays = sharedPreferences.getInt("dosageDays_$medicationName", 0)
            val notificationDelay = sharedPreferences.getInt("notificationDelay_$medicationName", 0)

            val recordTimeString = record.lines().find { it.startsWith("Fecha y Hora:") }
                ?.removePrefix("Fecha y Hora: ")
                ?.trim()

            if (recordTimeString != null) {
                val recordTime = try {
                    java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(recordTimeString)?.time ?: 0
                } catch (e: ParseException) {
                    e.printStackTrace()
                    continue
                }

                val currentTime = System.currentTimeMillis()
                val timeDifference = (currentTime - recordTime) / (1000 * 60 * 60) // Diferencia en horas

                if (timeDifference >= dosageFrequency) {
                    sendNotification(context, medicationName) // función para enviar la notificación
                }
            }
        }
    }
}


// Función para enviar notificaciones
    fun sendNotification(context: Context, medicacionNombre: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_reminder"

        // Crear un canal de notificación (solo necesario para API 26 y superior)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Medication Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo) // Asegúrate de tener un icono
            .setContentTitle("Recordatorio de Medicación")
            .setContentText("Es hora de tomar: $medicacionNombre")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build()) // ID de notificación
    }










