package pharmapro.carlosnava

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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Registros de Medicación",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de registros
        LazyColumn(
            modifier = Modifier.weight(1f) // para que la columna ocupe el espacio disponible
        ) {
            itemsIndexed(medicationRecords.value) { index, record ->
                val isSelected = selectedRecords.value.contains(index)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
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
                    // Línea separadora entre registros
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
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











