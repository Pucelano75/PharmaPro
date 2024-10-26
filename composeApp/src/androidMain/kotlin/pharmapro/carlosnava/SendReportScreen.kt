package pharmapro.carlosnava

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// Función de envío de informe
@Composable
fun SendReportScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enviar Informe de Medicación", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Introduce el correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (email.isNotEmpty()) {
                // Solicitar permisos de escritura en almacenamiento
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1
                    )
                } else {
                    val pdfFile = generatePdf() // Generar el PDF
                    if (pdfFile != null) {
                        openEmailClient(context, email, "Informe de Medicación", "Adjunto encontrarás el informe de tus tomas de medicación.", pdfFile)
                    } else {
                        Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Por favor, introduce un correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Enviar Informe")
        }
    }
}

private fun generatePdf(): File? {
    val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "reports")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File(dir, "informe_medicacion.pdf")
    return try {
        FileOutputStream(file).use { outputStream ->
            // Escribe en el archivo PDF (esto es solo un ejemplo)
            outputStream.write("Contenido del informe de medicación".toByteArray())
        }
        file
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun openEmailClient(context: Context, email: String, subject: String, body: String, file: File) {
    Log.d("EmailClient", "Abriendo cliente de correo para: $email") // Añade este log
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    // Crear el intent para enviar el correo
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // Añadir el email aquí
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Otorga permisos para leer el URI
    }

    // Comprobar si hay aplicaciones que pueden manejar este intent
    if (emailIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(emailIntent, "Enviar informe"))
    } else {
        Toast.makeText(context, "No hay aplicaciones disponibles para enviar el correo.", Toast.LENGTH_SHORT).show()
    }
}
