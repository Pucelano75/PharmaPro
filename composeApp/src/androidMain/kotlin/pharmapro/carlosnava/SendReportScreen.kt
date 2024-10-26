package pharmapro.carlosnava

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun SendReportScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título en negrita con estilo moderno
        Text(
            text = "Envio de registros",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Imagen decorativa de formulario desde drawable
        Image(
            painter = painterResource(id = R.drawable.formulario_diario),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón moderno con color gris claro
        ElevatedButton(
            onClick = {
                val pdfFile = generatePdf(context) // Genera el PDF
                if (pdfFile != null) {
                    Toast.makeText(context, "Informe generado correctamente", Toast.LENGTH_SHORT).show()
                    openShareIntent(context, pdfFile) // Abre el menú de compartir
                } else {
                    Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color(0xFFD3D3D3) // Gris claro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Preparar y enviar Informe", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun generatePdf(context: Context): File? {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(dir, "informe_medicacion.pdf")

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    // Contenido del PDF
    val canvas = page.canvas
    val paint = android.graphics.Paint()
    paint.textSize = 16f
    paint.isAntiAlias = true
    canvas.drawText("Informe de Medicación", 80f, 50f, paint)

    // Datos de ejemplo de RecordsScreen
    val reportContent = "Fecha: 2024-10-26\nMedicamento: Paracetamol\nDosis: 500 mg"
    canvas.drawText(reportContent, 80f, 100f, paint)

    pdfDocument.finishPage(page)

    return try {
        FileOutputStream(file).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        pdfDocument.close()
        null
    }
}

private fun openShareIntent(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    // Crear el intent para compartir el archivo
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Iniciar la actividad de compartir
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(shareIntent, "Compartir informe"))
    } else {
        Toast.makeText(context, "No hay aplicaciones disponibles para compartir el informe.", Toast.LENGTH_SHORT).show()
    }
}


