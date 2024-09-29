package pharmapro.carlosnava

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Usamos ModalNavigationDrawer para manejar el menú lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState, scope)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") }, // Dejar título vacío
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.padding(start = 16.dp)) { // Agregar padding
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Text("Contenido Principal", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp), // Mayor espaciado alrededor
        horizontalAlignment = Alignment.CenterHorizontally, // Centrar los botones horizontalmente
        verticalArrangement = Arrangement.Center // Centrar verticalmente
    ) {
        // Espaciador para separar el botón hamburguesa de los botones del menú
        Spacer(modifier = Modifier.height(100.dp))

        // Botón 1: Registrar Medicación
        Button(
            onClick = {
                navController.navigate("registerMedication")
                scope.launch { drawerState.close() } // Cerrar el menú
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Espacio entre los botones
        ) {
            Text(
                "Registrar Medicación",
                fontSize = 18.sp, // Tamaño de texto más grande
                fontWeight = FontWeight.Bold
            )
        }

        // Botón 2: Programación
        Button(
            onClick = {
                navController.navigate("programming")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                "Programación",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Botón 3: Registros
        Button(
            onClick = {
                navController.navigate("records")
                scope.launch { drawerState.close() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                "Registros",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


