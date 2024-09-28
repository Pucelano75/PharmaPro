package pharmapro.carlosnava

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PharmaPro",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de "Inicio de sesión" con bordes redondeados y colores personalizados
        Button(
            onClick = { /* Acción para inicio de sesión */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp), // Bordes redondeados
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.LightGray, // Fondo gris claro
                contentColor = Color.Black // Texto negro
            )
        ) {
            Text(text = "Inicio de sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de "Nuevo usuario" con bordes redondeados y colores personalizados
        Button(
            onClick = { /* Acción para nuevo usuario */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp), // Bordes redondeados
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray, // Fondo gris medio
                contentColor = Color.Black // Texto negro
            )
        ) {
            Text(text = "Nuevo usuario")
        }
    }
}
