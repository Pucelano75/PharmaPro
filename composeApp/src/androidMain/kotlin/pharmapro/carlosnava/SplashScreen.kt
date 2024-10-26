package pharmapro.carlosnava

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0.5f) }
    val context = LocalContext.current // Aquí se obtiene el contexto

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = EaseOutBounce)
        )
        delay(5000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color.LightGray, Color.White)))
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = pharmapro.carlosnava.R.drawable.logotipo),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .scale(scale.value),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Bienvenido a PharmaPro",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 30.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tu aplicación de salud con la que tendrás control total en la toma de tu medicación.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Enlace seleccionable
            ClickableText(
                text = AnnotatedString("Recuerda comprar tus pegatinas NFC en el siguiente enlace: https://n9.cl/mjb0z"),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.Blue
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = { offset ->
                    // Aquí se detecta el enlace y se abre el navegador
                    val text = "Recuerda comprar tus pegatinas NFC en el siguiente enlace: https://n9.cl/mjb0z"
                    if (offset in text.indices) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://n9.cl/mjb0z"))
                        context.startActivity(intent)
                    }
                }
            )
        }

        // Botón "Saltar"
        Text(
            text = "Saltar",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Blue,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .clickable {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
        )
    }
}

