package pharmapro.carlosnava

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
        // Logo y texto en la misma línea en la parte superior
        Row(
            modifier = Modifier
                .align(Alignment.TopStart) // Alineación en la parte superior izquierda
                .padding(start = 16.dp, top = 16.dp), // Espaciado a la izquierda y arriba
            verticalAlignment = Alignment.CenterVertically // Alinear verticalmente el logo y el texto
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo), // Asegúrate de que el logo esté en la carpeta drawable
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp), // Tamaño del logo
                contentScale = ContentScale.Crop
            )

            // Espacio entre el logo y el texto
            Spacer(modifier = Modifier.width(8.dp))

            // Texto de bienvenida
            Text(
                text = "Bienvenido a PharmaPro",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 30.sp, // Tamaño del texto
                    color = androidx.compose.ui.graphics.Color.DarkGray // Color gris oscuro
                )
            )
        }

        // Texto de descripción centrado debajo del logo y el título
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp), // Ajusta la separación del texto con el logo
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Alineación en la parte superior
        ) {
            // Añadir espacio entre el título y la descripción
            Spacer(modifier = Modifier.height(32.dp)) // Espacio entre los textos

            Text(
                text = "Tu aplicación de salud con la que tendrás control total en la toma de tu medicación.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                modifier = Modifier.padding(horizontal = 16.dp) // Espaciado horizontal
            )
        }
    }
}

