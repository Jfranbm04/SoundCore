package modelos

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import vistas.AjustesScreen
import vistas.BuscarScreen
import vistas.EditarPerfilScreen
import vistas.HomeScreen
import vistas.ListaAmigosScreen
import vistas.LoginScreen
import vistas.MainScreen
import vistas.PerfilScreen
import vistas.PerfilUsuarioScreen
import vistas.RankingAmigosScreen
import vistas.RankingGlobalScreen
import vistas.RankingLocalScreen
import vistas.SignUpScreen
import vistas.SolicitudesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Verificar si ya se ha iniciado sesión
    val pantallaInicial = if (FirebaseAuth.getInstance().currentUser != null) {
        Paths.pantallaPrincipal.path
    } else {
        Paths.login.path
    }

    NavHost(navController = navController, startDestination = pantallaInicial) {
        composable(route = Paths.login.path) {
            LoginScreen(navController)   // Método que pinta la pantalla del login
        }
        composable(route = Paths.signUp.path) {
            SignUpScreen(navController)  // Método que pinta la pantalla de registrarse
        }
        composable(route = Paths.pantallaPrincipal.path) {
            MainScreen(navController)  // Método que pinta la pantalla principal
        }
        composable(route = BottomNavItem.Home.route) { HomeScreen(navController) }
        composable(route = BottomNavItem.Buscar.route) { BuscarScreen(navController) }
        composable(route = BottomNavItem.Perfil.route) { PerfilScreen(navController) }
        composable(route = Paths.Ajustes.path) { AjustesScreen(navController) }
        composable(route = Paths.EditarPerfil.path) { EditarPerfilScreen(navController) }
        composable(route = Paths.Solicitudes.path) { SolicitudesScreen(navController) }
        composable(route = Paths.ListaAmigos.path) { ListaAmigosScreen(navController) }
        composable(route = Paths.RankingLocal.path) { RankingLocalScreen(navController) }
        composable(route = Paths.RankingAmigos.path) { RankingAmigosScreen(navController) }
        composable(route = Paths.RankingGlobal.path) { RankingGlobalScreen(navController) }
        composable(
            route = "${Paths.PerfilUsuario.path}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            PerfilUsuarioScreen(navController, backStackEntry.arguments?.getString("userId") ?: "")
        }
    }
}


