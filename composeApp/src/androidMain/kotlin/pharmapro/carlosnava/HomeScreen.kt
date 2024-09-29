package pharmapro.carlosnava

import android.nfc.NfcAdapter
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current // Obtener el contexto desde el Composable
    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    var nfcDetected by remember { mutableStateOf(false) }
    var nfcMessage by remember { mutableStateOf("Acerque su dispositivo a la medicación para registrar la toma.") }

    // Callback para cuando se detecte una etiqueta NFC
    val nfcCallback = NfcAdapter.ReaderCallback { tag ->
        // Acción cuando la etiqueta NFC es detectada
        nfcMessage = "Etiqueta NFC detectada. Registro de toma exitoso."
        nfcDetected = true
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
                    title = { Text("") }, // Dejar título vacío
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.padding(start = 16.dp)) { // Agregar padding
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {

                // Mostrar el mensaje de NFC
                Text(
                    text = nfcMessage, // Asegúrate de pasar 'text' como argumento
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (nfcDetected) Color.Green else Color.Black
                )
            }
        }
    }
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp), // Mayor espaciado alrededor
        horizontalAlignment = Alignment.CenterHorizontally, // Centrar los botones horizontalmente
        verticalArrangement = Arrangement.Center // Centrar verticalmente
    ) {
        // Espaciador para separar el botón hamburguesa de los botones del menú
        Spacer(modifier = Modifier.height(100.dp))

        // Botón 1: Registrar Medicación
        Button(
            onClick = {
                navController.navigate("registerMedication")
                scope.launch { drawerState.close() } // Cerrar el menú
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Espacio entre los botones
        ) {
            Text(
                "Registrar Medicación",
                fontSize = 18.sp, // Tamaño de texto más grande
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



