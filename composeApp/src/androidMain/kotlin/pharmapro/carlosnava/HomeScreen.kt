package pharmapro.carlosnava

import android.content.Context
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.tech.Ndef
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable

fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Column(
        modifier = Modifier
            .wrapContentSize() // Cambiado a wrapContentSize para que ocupe solo el espacio necesario
            .padding(16.dp)
            .background(Color.LightGray), // Fondo gris claro
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Ancho deseado para los botones (ajusta según sea necesario)
        val buttonWidth = 280.dp

        // Botón para registrar medicación
        Button(
            onClick = {
                navController.navigate("registerMedication")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.width(buttonWidth).padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp), // Esquinas redondeadas más pronunciadas
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)) // Color de fondo personalizado
        ) {
            Text(text = "Registrar medicación", fontSize = 18.sp, color = Color.White) // Texto blanco
        }

        // Botón para programación
        Button(
            onClick = {
                navController.navigate("programming")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.width(buttonWidth).padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Programación", fontSize = 18.sp, color = Color.White)
        }

        // Botón para registros
        Button(
            onClick = {
                navController.navigate("records")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.width(buttonWidth).padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Registros", fontSize = 18.sp, color = Color.White)
        }

        // Botón para cerrar el menú
        Button(
            onClick = {
                scope.launch { drawerState.close() }
            },
            modifier = Modifier.width(buttonWidth).padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text(text = "Cerrar menú", fontSize = 18.sp, color = Color.White)
        }
    }
}


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

    // Callback para cuando se detecte una etiqueta NFC
    val nfcCallback = NfcAdapter.ReaderCallback { tag ->
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            val records = ndefMessage.records

            if (records.isNotEmpty()) {
                val payload = records[0].payload
                val message = String(payload, Charset.forName("UTF-8"))
                val parts = message.split(";")
                val medicationName = parts.getOrNull(0) ?: "Desconocido"
                val reason = parts.getOrNull(1) ?: "Desconocido"

                val currentDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
                nfcMessage = "Medicamento: $medicationName\nMotivo: $reason"
                nfcDetected = true

                val editor = sharedPreferences.edit()
                val record = "Medicamento: $medicationName\nMotivo: $reason\nFecha y Hora: $currentDateTime\n\n"
                editor.putString("medicationRecords", (sharedPreferences.getString("medicationRecords", "") ?: "") + record)
                editor.apply()
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState, scope)  // Usar el DrawerContent
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp) ){
                            Image(
                                painter = painterResource(id = pharmapro.carlosnava.R.drawable.logo),
                                contentDescription = "Logo de PharmaPro",
                                modifier = Modifier.size(50.dp) // Tamaño más pequeño para el logo
                            )
                            Spacer(modifier = Modifier.width(12.dp)) // Espacio entre el logo y el texto
                            Text("PharmaPro", fontWeight = FontWeight.Bold, fontSize = 34.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }, // Abrir el Drawer
                            modifier = Modifier.padding(start = 8.dp)
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
                    .wrapContentSize(Alignment.TopStart)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Bienvenido a PharmaPro",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = nfcMessage,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}














