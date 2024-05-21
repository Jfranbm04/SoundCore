package vistas

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.soundcore.R
import com.example.soundcore.ui.theme.SoundCoreTheme
import com.example.soundcore.ui.theme.backgroundOscuro

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.azul2
import com.example.soundcore.ui.theme.azul3
import com.example.soundcore.ui.theme.azul4
import com.example.soundcore.ui.theme.backgroundClaro
import modelos.BottomNavItem

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
                        // Evita la duplicación de la misma ruta en el back stack
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Evita la recreación del mismo destino si ya está seleccionado
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}




