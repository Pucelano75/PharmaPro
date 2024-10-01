package pharmapro.carlosnava

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext

@Composable
fun RecordsScreen() {
    val context = LocalContext.current
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("PharmaPro", Context.MODE_PRIVATE)

    // Obtener los registros de medicación guardados
    val medicationRecords = sharedPreferences.getString("medicationRecords", "No hay registros.")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Registros de Medicación",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = medicationRecords ?: "No hay registros.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    }
}




