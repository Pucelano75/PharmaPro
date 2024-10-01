package pharmapro.carlosnava

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("PharmaProData", Context.MODE_PRIVATE)

    // Recuperamos los registros almacenados en SharedPreferences
    val records = sharedPreferences.getStringSet("medicationRecords", mutableSetOf()) ?: mutableSetOf()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de Medicación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (records.isEmpty()) {
                Text("No hay registros disponibles", fontSize = 16.sp)
            } else {
                records.forEach { record ->
                    Text(text = record, fontSize = 14.sp)
                }
            }
        }
    }
}

