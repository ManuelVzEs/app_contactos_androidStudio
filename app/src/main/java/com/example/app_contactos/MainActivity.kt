package com.example.app_contactos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_contactos.ui.theme.App_contactosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_contactosTheme {
                AppNavegacion()
            }
        }
    }
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = remember { DatabaseHelper(context) }

    NavHost(navController = navController, startDestination = "lista") {

        composable("lista") {
            ListaContactosScreen(
                db = db,
                onAgregar = { navController.navigate("formulario/-1") },
                onContactoClick = { id -> navController.navigate("detalle/$id") }
            )
        }

        composable(
            "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStack ->
            val id = backStack.arguments?.getInt("id") ?: return@composable
            DetalleContactoScreen(
                db = db,
                contactoId = id,
                onVolver = { navController.popBackStack() },
                onEditar = { navController.navigate("formulario/$id") }
            )
        }

        composable(
            "formulario/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStack ->
            val id = backStack.arguments?.getInt("id") ?: -1
            FormularioContactoScreen(
                db = db,
                contactoId = id,
                onGuardar = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}