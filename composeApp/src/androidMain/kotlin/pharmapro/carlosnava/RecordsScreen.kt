package pharmapro.carlosnava

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecordsScreen() {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)

    // Obtener los registros de medicación guardados
    val medicationRecordsString = sharedPreferences.getString("medicationRecords", "") ?: ""
    val medicationRecords = medicationRecordsString.split("\n\n").filter { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Registros de Medicación",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de desplazamiento
        LazyColumn(
            modifier = Modifier.weight(1f) // Asigna el peso para llenar el espacio disponible
        ) {
            items(medicationRecords) { record ->
                Column {
                    Text(
                        text = record,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.Gray)) // Como separador
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para eliminar todos los registros
        Button(
            onClick = {
                val editor = sharedPreferences.edit()
                editor.remove("medicationRecords") // Elimina los registros
                editor.apply() // Aplicar cambios
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Eliminar Todos los Registros",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



