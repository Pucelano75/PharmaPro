package pharmapro.carlosnava.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definición de colores
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Color primario
    onPrimary = Color.White,      // Color del texto sobre el primario
    secondary = Color(0xFF03DAC5) // Color secundario
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // Color primario
    onPrimary = Color.Black,      // Color del texto sobre el primario
    secondary = Color(0xFF03DAC5) // Color secundario
)

@Composable
fun PharmaProTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de definir Typography si lo necesitas
        content = content
    )
}
