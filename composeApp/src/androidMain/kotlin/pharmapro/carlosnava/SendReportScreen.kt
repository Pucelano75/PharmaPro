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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun SendReportScreen(navController: NavHostController) {
    val context = LocalContext.current

    // Inicializa Mobile Ads
    LaunchedEffect(Unit) {
        MobileAds.initialize(context) {}
    }

    // Crea un AdView para el banner
    val adView = remember { AdView(context).apply {
        adUnitId = "ca-app-pub-3940256099942544/6300978111" // Reemplaza con tu ID de anuncio
        setAdSize(AdSize.BANNER)
    }}

    // Cargar el anuncio
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Envio de registros",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.formulario_diario),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

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
            Text("Generar Informe", style = MaterialTheme.typography.bodyLarge)
        }
        // Banner Ad
        Spacer(modifier = Modifier.height(16.dp)) // Espaciado antes del anuncio
        AndroidView(factory = { adView }) // Mostrar el banner
    }
}

private fun generatePdf(context: Context): File? {
    val sharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)

    val medicationRecords = sharedPreferences.getString("medicationRecords", "")
        ?.split("\n\n")
        ?.filter { it.isNotEmpty() } ?: emptyList()

    val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(dir, "informe_medicacion.pdf")

    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    // Contenido del PDF
    val canvas = page.canvas
    val paint = android.graphics.Paint().apply {
        textSize = 16f
        isAntiAlias = true
    }

    canvas.drawText("Informe de Tomas:", 80f, 50f, paint)

    // Agregar los registros al PDF con ajuste de línea
    var yPosition = 100f
    medicationRecords.forEach { record ->
        val lines = wrapText(record, paint, 500f) // Ajustar según el ancho del PDF
        for (line in lines) {
            canvas.drawText(line, 80f, yPosition, paint)
            yPosition += 20f // Espacio entre líneas
        }
        yPosition += 10f // Espacio adicional entre registros
    }

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

// Función para ajustar el texto
private fun wrapText(text: String, paint: android.graphics.Paint, maxWidth: Float): List<String> {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = ""

    for (word in words) {
        val testLine = "$currentLine $word".trim()
        if (paint.measureText(testLine) > maxWidth) {
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine) // Añadir línea anterior a la lista
            }
            currentLine = word // Comenzar nueva línea
        } else {
            currentLine = testLine // Añadir palabra a la línea actual
        }
    }
    if (currentLine.isNotEmpty()) {
        lines.add(currentLine) // Añadir última línea
    }

    return lines
}

private fun openShareIntent(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(shareIntent, "Compartir informe"))
    } else {
        Toast.makeText(context, "No hay aplicaciones disponibles para compartir el informe.", Toast.LENGTH_SHORT).show()
    }
}



