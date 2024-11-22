package pharmapro.carlosnava

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0.5f) }
    val context = LocalContext.current // Aquí se obtiene el contexto

    // Inicializa Mobile Ads
    LaunchedEffect(Unit) {
        MobileAds.initialize(context) {}
    }

    // Crea un AdView para el banner
    val adView = remember { AdView(context).apply {
        adUnitId = "ca-app-pub-5792195439781648/9789140004" // Reemplaza con tu ID de anuncio
        setAdSize(AdSize.BANNER)
    }}

    // Cargar el anuncio
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
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

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tu aplicación de salud con la que tendrás control total en la toma de tu medicación.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Añadir el banner de AdMob justo debajo del texto
            Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre el texto y el anuncio
            AndroidView(factory = { adView })

            // Espacio flexible para empujar el botón hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // Botón "Saltar"
            Text(
                text = "Saltar",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Blue,
                    fontSize = 18.sp
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
            )
        }
    }
}


