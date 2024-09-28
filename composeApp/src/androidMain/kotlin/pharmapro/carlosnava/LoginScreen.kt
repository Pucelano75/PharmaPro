package pharmapro.carlosnava

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current // Obtener el contexto
    val userPreferences = UserPreferences(context) // Instancia de UserPreferences


    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de nombre de usuario
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                TextButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Text(if (passwordVisibility) "Ocultar" else "Mostrar")
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de inicio de sesión
        Button(
            onClick = {
                val (storedUsername, storedPassword) = userPreferences.getUserData()

                    if (username == storedUsername && password == storedPassword) {
                        Toast.makeText(context, "Inicio correcto", Toast.LENGTH_SHORT).show()
                        // Navegar a la pantalla principal
                        navController.navigate("main")
                    } else {
                        Toast.makeText(context, "Usuario incorrecto", Toast.LENGTH_SHORT).show()
                        // Regresar a MainScreen
                        navController.navigate("main")
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}

