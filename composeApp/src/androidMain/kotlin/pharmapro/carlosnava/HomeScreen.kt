package pharmapro.carlosnava

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Text("Menú") // Texto en lugar de un ícono
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
        modifier = Modifier.padding(16.dp)
    ) {
        TextButton(onClick = {
            navController.navigate("registerMedication")
            scope.launch { drawerState.close() } // Cerrar el menú
        }) {
            Text("Registrar Medicación")
        }

        TextButton(onClick = {
            navController.navigate("programming")
            scope.launch { drawerState.close() }
        }) {
            Text("Programación")
        }

        TextButton(onClick = {
            navController.navigate("records")
            scope.launch { drawerState.close() }
        }) {
            Text("Registros")
        }
    }
}

