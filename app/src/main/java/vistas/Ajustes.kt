package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.azul2
import com.example.soundcore.ui.theme.azul3
import com.example.soundcore.ui.theme.azul4
import com.example.soundcore.ui.theme.backgroundClaro
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import modelos.Paths

@Composable
fun AjustesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ajustes",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                backgroundColor = backgroundOscuro,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Salir", tint = Color.White)
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundOscuro),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Ajustes", color = Color.White)

                // Botón para cerrar sesión
                Button(
                    onClick = {
                        // Cerrar sesión y volver a login
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Paths.login.path)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(text = "CERRAR SESIÓN", color = Color.White)
                }

            }


        }
    )
}