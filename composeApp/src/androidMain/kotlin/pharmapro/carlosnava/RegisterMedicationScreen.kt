package pharmapro.carlosnava

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.material3.SnackbarDuration // Asegúrate de tener esta importación

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMedicationScreen(navController: NavController) {
    var medicationName by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Medicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Nombre de la Medicación:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = medicationName,
                onValueChange = { medicationName = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text("Motivo para su Toma:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text("Observaciones:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = observations,
                onValueChange = { observations = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(2.dp))

            Button(
                onClick = {
                    saveMedicationData(navController.context, medicationName, reason, observations)

                    // Mostrar Snackbar de confirmación dentro de una coroutine
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Datos guardados correctamente.",
                            duration = SnackbarDuration.Long // Aumenta la duración
                        )
                    }

                    navController.popBackStack()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }
        }
    }
}

// Función para guardar los datos en SharedPreferences
fun saveMedicationData(context: Context, name: String, reason: String, observations: String) {
    val sharedPreferences = context.getSharedPreferences("MedicationPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("medicationName", name)
        putString("reason", reason)
        putString("observations", observations)
        apply()
    }
}

// Función para cargar los datos desde SharedPreferences (opcional)
fun loadMedicationData(context: Context): Triple<String?, String?, String?> {
    val sharedPreferences = context.getSharedPreferences("MedicationPrefs", Context.MODE_PRIVATE)
    val name = sharedPreferences.getString("medicationName", null)
    val reason = sharedPreferences.getString("reason", null)
    val observations = sharedPreferences.getString("observations", null)
    return Triple(name, reason, observations)
}

