package vistas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.example.soundcore.ui.theme.azul3
import com.example.soundcore.ui.theme.azul4
import com.example.soundcore.ui.theme.backgroundClaro
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import modelos.Paths

@Composable
fun PerfilScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontSize = 20.sp, color= Color.White, fontWeight = FontWeight.Bold) },
                backgroundColor = backgroundOscuro,
                actions = {
                    IconButton(onClick = { navController.navigate("ajustes") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.White)
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
                currentUser?.let { user ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Muestra el nombre del usuario
                        Text(text = "Nombre: ${user.displayName ?: "No disponible"}", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Muestra el correo electrónico del usuario
                        Text(text = "Correo electrónico: ${user.email ?: "No disponible"}", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Muestra la foto de perfil del usuario si está disponible
//                        user.photoUrl?.let { photoUrl ->
//                            Image(
//                                painter = rememberImagePainter(photoUrl),
//                                contentDescription = "Foto de perfil",
//                                modifier = Modifier.size(100.dp).clip(CircleShape)
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                        }
                    }
                }
            }
        }
    )
}
