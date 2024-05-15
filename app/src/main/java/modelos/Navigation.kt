package modelos

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vistas.LoginScreen
import vistas.SignUpScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Paths.login.path) {
        composable(route = Paths.login.path) {
            LoginScreen(navController)   // Método que pinta la pantalla del login
        }
        composable(route = Paths.signUp.path) {
            SignUpScreen(navController)  // Método que pinta la pantalla de registrarse
        }
    }
}

