package pharmapro.carlosnava

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.tech.Ndef
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.nio.charset.Charset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMedicationScreen(navController: NavController) {
    var medicationName by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var nfcMessage by remember { mutableStateOf("Acerque una etiqueta NFC para guardar los datos.") }
    var isWritingNfc by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

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
        }
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nfcAdapter != null) {
                        isWritingNfc = true
                        nfcMessage = "Acércate una etiqueta NFC para escribir los datos."
                    } else {
                        nfcMessage = "El dispositivo no soporta NFC."
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(nfcMessage, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

            // Lógica para escribir en una etiqueta NFC cuando esté disponible
            if (isWritingNfc) {
                LaunchedEffect(Unit) {
                    nfcAdapter?.enableReaderMode(
                        context as android.app.Activity,
                        { tag ->
                            val ndef = Ndef.get(tag)
                            if (ndef != null) {
                                ndef.connect()
                                val message = "$medicationName;$reason"
                                val ndefMessage = NdefMessage(
                                    arrayOf(
                                        NdefRecord.createTextRecord("en", message)
                                    )
                                )
                                ndef.writeNdefMessage(ndefMessage)
                                ndef.close()
                                nfcMessage = "Datos guardados en la etiqueta NFC con éxito."
                                isWritingNfc = false
                            }
                        },
                        NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B,
                        null
                    )
                }
            }
        }
    }
}


