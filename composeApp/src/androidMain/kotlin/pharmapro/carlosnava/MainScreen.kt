package pharmapro.carlosnava
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
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
            painter = painterResource(id = R.drawable.logotipo),
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        // Botón "Nuevo Usuario"
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp), // Bordes redondeados
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray, // Fondo gris claro
                contentColor = Color.Black // Texto negro
            )
        ) {
            Text("Nuevo Usuario")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Agregamos un espacio entre los botones


        // Botón de "Inicio de sesión" con bordes redondeados y colores personalizados
        Button(
            onClick = { navController.navigate("login")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp), // Bordes redondeados
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray, // Fondo gris claro
                contentColor = Color.Black // Texto negro
            )
        ) {
            Text("Inicio de sesión")
        }
    }
}




