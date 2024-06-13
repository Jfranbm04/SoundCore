package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.backgroundOscuro

import androidx.compose.material3.Icon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import modelos.BottomNavItem

// Esta pantalla sirve para alojar a todas las demás
@Composable
fun MainScreen(navController: NavController) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(mainNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding).background(backgroundOscuro)
        ) {
            composable(route = BottomNavItem.Home.route) { HomeScreen(navController) }
            composable(route = BottomNavItem.Buscar.route) { BuscarScreen(navController) }
            composable(route = BottomNavItem.Perfil.route) { PerfilScreen(navController) }
        }
    }
}

// Define BottomNavigationBar
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Buscar,
        BottomNavItem.Perfil
    )
    BottomNavigation(
        backgroundColor = backgroundOscuro,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            val selected = navController.currentBackStackEntry?.destination?.route == item.route
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label, tint = Color.White, modifier = Modifier.size(30.dp)) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        // Esto evita la duplicación de la misma ruta en el back stack
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}




