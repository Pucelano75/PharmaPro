package pharmapro.carlosnava

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class SendReportWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        // Genera el informe en PDF
        val reportFile = createPdfReport()

        // Envía el informe por WhatsApp
        reportFile?.let {
            sendReportViaWhatsApp(it)
        }

        return Result.success()
    }

    private fun createPdfReport(): File? {
        return try {
            // Crea el directorio para el informe si no existe
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "PharmaProReports")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Nombre del archivo
            val file = File(directory, "informe_diario.pdf")

            // Crear el archivo PDF
            val outputStream: OutputStream = FileOutputStream(file)
            val document = PdfDocument(PdfWriter(outputStream))

            // Agrega contenido al PDF
            val documentPage = document.addNewPage()
            val canvas = PdfCanvas(documentPage)

            // Crea la fuente Helvetica
            val font: PdfFont = PdfFontFactory.createFont("Helvetica", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED)

            canvas.beginText()
            canvas.setFontAndSize(font, 12f) // Utiliza la fuente Helvetica aquí
            canvas.showText("Informe Diario de Medicación")
            canvas.endText()

            // Aquí puedes agregar más contenido al PDF, como registros de medicación

            document.close() // No olvides cerrar el documento

            file // Devuelve el archivo creado
        } catch (e: Exception) {
            e.printStackTrace()
            null // En caso de error, devuelve null
        }
    }

    private fun sendReportViaWhatsApp(reportFile: File) {
        val context = applicationContext
        // Obtén la URI del archivo utilizando FileProvider
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", reportFile)

        // Crea un Intent para enviar el archivo por WhatsApp
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Aquí está su informe diario de medicación.")
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Necesario para permitir el acceso al archivo
            setPackage("com.whatsapp") // Solo para WhatsApp
        }

        // Verifica si hay una aplicación de WhatsApp instalada
        if (sendIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendIntent)
        } else {
            // Manejo si WhatsApp no está instalado
            // Puedes mostrar un mensaje o un diálogo
        }
    }
}



