package pharmapro.carlosnava

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Lanzar el efecto para navegar después del retraso
    LaunchedEffect(Unit) {
        delay(10000) // Esperar 10 segundos
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Diseño de la pantalla de bienvenida
    Box(modifier = Modifier.fillMaxSize()) {
        // Logo en la esquina superior izquierda
        Image(
            painter = painterResource(id = R.drawable.logo), // Asegúrate de que el logo esté en la carpeta drawable
            contentDescription = null,
            modifier = Modifier
                .size(60.dp) // Tamaño del logo
                .padding(start = 16.dp, top = 16.dp), // Espaciado en la esquina superior izquierda
            contentScale = ContentScale.Crop
        )

        // Texto centrado en la pantalla
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido a PharmaPro",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp),
                modifier = Modifier.padding(top = 16.dp) // Espaciado arriba del texto
            )
            Text(
                text = "Tu aplicación de salud con la que tendrás control total en la toma de tu medicación.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.padding(horizontal = 16.dp) // Espaciado horizontal para que el texto no esté pegado a los bordes
            )
        }
    }
}
