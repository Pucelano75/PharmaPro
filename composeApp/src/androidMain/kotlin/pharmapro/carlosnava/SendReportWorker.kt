package pharmapro.carlosnava

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File


class SendReportWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val phoneNumber = inputData.getString("PHONE_NUMBER") ?: return Result.failure()
        val formattedPhoneNumber = phoneNumber.replace("+", "").replace(" ", "")

        // Generar el informe en PDF
        val reportFile = createPdfReport()

        // Verifica que el archivo PDF se generó correctamente
        return if (reportFile != null) {
            sendReportViaWhatsApp(reportFile, formattedPhoneNumber)
            Result.success() // Retorna el resultado exitoso
        } else {
            Log.e("SendReportWorker", "Error al generar el PDF.")
            Result.failure() // Si no se generó el PDF, retorna un fallo
        }
    }

    private fun createPdfReport(): File? {
        val pdfDir = File(applicationContext.filesDir, "pdfs")
        if (!pdfDir.exists()) {
            pdfDir.mkdir() // Crea el directorio si no existe
        }

        val pdfFile = File(pdfDir, "medication_report.pdf")

        // Implementa la lógica para generar el PDF aquí
        pdfFile.writeText("Este es un informe de ejemplo para la medicación")

        return pdfFile
    }

    private fun sendReportViaWhatsApp(reportFile: File, phoneNumber: String) {
        val context = applicationContext
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", reportFile)

        Log.d("SendReportWorker", "Enviando informe a: $phoneNumber")

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Aquí está su informe diario de medicación.")
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
            putExtra("jid", "$phoneNumber@s.whatsapp.net") // Formato correcto para WhatsApp
        }

        if (sendIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(sendIntent)
        } else {
            Log.e("SendReportWorker", "WhatsApp no está instalado.")
            // Aquí podrías manejar el caso en que WhatsApp no esté disponible
        }
    }
}


