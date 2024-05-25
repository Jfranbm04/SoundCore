package vistas
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.R
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.azul2
import com.example.soundcore.ui.theme.azul3
import com.example.soundcore.ui.theme.azul4
import com.example.soundcore.ui.theme.backgroundClaro
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import controladores.obtenerDatosUsuario
import kotlinx.coroutines.launch
import modelos.Paths



@Composable
fun PerfilScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            coroutineScope.launch {
                userData = obtenerDatosUsuario(uid)
                isLoading = false
            }
        } ?: run {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold) },
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
                contentAlignment = Alignment.TopStart,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(8.dp))
                } else {
                    userData?.let { data ->
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)) {
                            // Fila para foto de perfil, nombre y correo electrónico
                            Row(verticalAlignment = Alignment.Top) {
                                // Mostrar un círculo azul claro en lugar de la foto de perfil
                                Image(
                                    painter = painterResource(id = R.drawable.google_logo),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.size(80.dp) // Tamaño de la imagen
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Muestra el nombre y el correo electrónico del usuario
                                Column {
                                    Text(text = "${data["nombreUsuario"] ?: "Nombre no disponible"}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${data["email"] ?: "Correo no disponible"}", color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Editar perfil y Estadísticas
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(
                                    onClick = { /* Navegar a la pantalla de perfil del usuario */ },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFF1565C0)
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(text = "Editar perfil", color = Color.White)
                                }

                                Button(
                                    onClick = { /* Navegar a la pantalla de estadísticas del usuario */ },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFF1565C0)
                                    )
                                ) {
                                    Text(text = "Estadísticas", color = Color.White)
                                }
                            }
                        }
                    } ?: run {
                        Text(text = "Error al cargar datos del usuario", color = Color.White)
                    }
                }
            }
        }
    )
}
























//@Composable
//fun PerfilScreen(navController: NavController) {
//    val currentUser = FirebaseAuth.getInstance().currentUser
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Perfil", fontSize = 20.sp, color= Color.White, fontWeight = FontWeight.Bold) },
//                backgroundColor = backgroundOscuro,
//                actions = {
//                    IconButton(onClick = { navController.navigate("ajustes") }) {
//                        Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = Color.White)
//                    }
//                }
//            )
//        },
//        content = { paddingValues ->
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//                    .background(backgroundOscuro),
//                contentAlignment = Alignment.Center,
//            ) {
//                currentUser?.let { user ->
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        // Muestra el nombre del usuario
//                        Text(text = "Nombre: ${user.displayName ?: "No disponible"}", color = Color.White)
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        // Muestra el correo electrónico del usuario
//                        Text(text = "Correo electrónico: ${user.email ?: "No disponible"}", color = Color.White)
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        // Muestra la foto de perfil del usuario si está disponible
////                        user.photoUrl?.let { photoUrl ->
////                            Image(
////                                painter = rememberImagePainter(photoUrl),
////                                contentDescription = "Foto de perfil",
////                                modifier = Modifier.size(100.dp).clip(CircleShape)
////                            )
////                            Spacer(modifier = Modifier.height(8.dp))
////                        }
//                    }
//                }
//            }
//        }
//    )
//}