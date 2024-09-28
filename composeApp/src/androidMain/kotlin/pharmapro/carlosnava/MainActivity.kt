package pharmapro.carlosnava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Usa solo savedInstanceState aquí
        setContent {
            MainScreen() // Llama a MainScreen
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MainScreen() // Llama a MainScreen
}
