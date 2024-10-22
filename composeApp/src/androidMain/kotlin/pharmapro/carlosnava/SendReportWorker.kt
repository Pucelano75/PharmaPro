package pharmapro.carlosnava

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File

class SendReportWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        // Obtener el número de teléfono ingresado desde la entrada de datos
        val phoneNumber = inputData.getString("PHONE_NUMBER") ?: return Result.failure()

        // Asegurarse de que el número esté en el formato internacional adecuado
        val formattedPhoneNumber = phoneNumber.replace("+", "").replace(" ", "")

        // Genera el informe en PDF
        val reportFile = createPdfReport()

        // Envía el informe por WhatsApp al número ingresado
        reportFile?.let {
            sendReportViaWhatsApp(it, formattedPhoneNumber)
        }

        return Result.success()
    }

    // Método para generar el archivo PDF (aquí deberías implementar la lógica real para crear el informe)
    private fun createPdfReport(): File? {
        // Define dónde se va a guardar el PDF
        val pdfDir = File(applicationContext.filesDir, "pdfs")
        if (!pdfDir.exists()) {
            pdfDir.mkdir()
        }

        // Crear archivo de reporte PDF
        val pdfFile = File(pdfDir, "medication_report.pdf")

        // Aquí debes implementar la lógica para generar el PDF
        // Ejemplo: escribir datos en el archivo (simplificado)
        pdfFile.writeText("Este es un informe de ejemplo para la medicación")

        return pdfFile
    }

    // Método para enviar el informe PDF por WhatsApp
    private fun sendReportViaWhatsApp(reportFile: File, phoneNumber: String) {
        val context = applicationContext
        // Crear la URI utilizando el FileProvider
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", reportFile)

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Aquí está su informe diario de medicación.")
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
            // Usar el número de teléfono en el formato que espera WhatsApp
            putExtra("jid", "$phoneNumber@s.whatsapp.net") // WhatsApp requiere el formato jid
        }

        // Verificar si WhatsApp está instalado antes de intentar enviar el informe
        if (sendIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendIntent)
        } else {
            // Mostrar un mensaje si WhatsApp no está instalado
            Toast.makeText(context, "WhatsApp no está instalado en este dispositivo.", Toast.LENGTH_SHORT).show()
        }
    }
}




