package pharmapro.carlosnava

import android.content.Context
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    // Almacenar en SharedPreferences
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)

    var nfcDetected by remember { mutableStateOf(false) }
    var nfcMessage by remember { mutableStateOf("Acerque su dispositivo a la medicación para registrar la toma.") }
    var nfcDetails by remember { mutableStateOf("") }

    // Callback para cuando se detecte una etiqueta NFC
    val nfcCallback = NfcAdapter.ReaderCallback { tag ->
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            val records = ndefMessage.records

            // Verificar si el mensaje tiene datos
            if (records.isNotEmpty()) {
                val payload = records[0].payload
                val message = String(payload, Charset.forName("UTF-8"))

                // Asumimos que el mensaje está en formato "medicationName;reason"
                val parts = message.split(";")
                val medicationName = parts.getOrNull(0) ?: "Desconocido"
                val reason = parts.getOrNull(1) ?: "Desconocido"

                // Obtener la fecha y hora actuales
                val currentDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                // Mostrar los datos leídos de la tarjeta NFC
                nfcMessage = "Medicamento: $medicationName\nMotivo: $reason"
                nfcDetected = true

                // Guardar los datos en SharedPreferences
                val sharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val record = "Medicamento: $medicationName\nMotivo: $reason\nFecha y Hora: $currentDateTime\n\n"
                editor.putString("medicationRecords", (sharedPreferences.getString("medicationRecords", "") ?: "") + record)
                editor.apply() // Aplicar cambios

            } else {
                nfcMessage = "No se encontraron datos en la etiqueta NFC."
            }

            ndef.close()
        }
    }

    // Configura el adaptador NFC si está disponible
    LaunchedEffect(Unit) {
        if (nfcAdapter != null && context is android.app.Activity) {
            nfcAdapter.enableReaderMode(
                context,
                nfcCallback,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B,
                null
            )
        }
    }

    // Usamos ModalNavigationDrawer para manejar el menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState, scope)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Mostrar los detalles del NFC
                    if (nfcDetected) {
                        Text(
                            text = nfcDetails,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }

                    // Mostrar el mensaje de confirmación del registro
                    Text(
                        text = nfcMessage,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (nfcDetected) Color.Green else Color.Black,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

// Función para guardar los detalles del medicamento en SharedPreferences
private fun saveMedicationDetails(sharedPreferences: SharedPreferences, medicationName: String, reason: String) {
    val editor = sharedPreferences.edit()
    val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    editor.putString("lastMedication", "$medicationName;$reason;$currentDate")
    editor.apply()
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Botón 1: Registrar Medicación
        Button(
            onClick = {
                navController.navigate("registerMedication")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                "Registrar Medicación",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Botón 2: Programación
        Button(
            onClick = {
                navController.navigate("programming")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                "Programación",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Botón 3: Registros
        Button(
            onClick = {
                navController.navigate("records")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                "Registros",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}




