package vistas
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.MailOutline
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.google.firebase.storage.FirebaseStorage
import controladores.descargarImagen
import controladores.obtenerDatosUsuario
import controladores.obtenerUrlFotoPerfil
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import modelos.Paths



@Composable
fun PerfilScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var fotoPerfilBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            coroutineScope.launch {
                userData = obtenerDatosUsuario(uid)
                val fotoPerfilUrl = obtenerUrlFotoPerfil(uid)
                fotoPerfilUrl?.let {
                    // Descargar la imagen de Firebase Storage
                    fotoPerfilBitmap = descargarImagen(it)
                }
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
                    IconButton(onClick = { navController.navigate("solicitudes") }) {
                        Icon(Icons.Default.MailOutline, contentDescription = "Solicitudes de amistad", tint = Color.White)
                    }
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
                    .background(backgroundOscuro)
                    .padding(paddingValues),
                contentAlignment = Alignment.TopStart,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(8.dp))
                } else {
                    userData?.let { data ->
                        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                fotoPerfilBitmap?.let { bitmap ->
                                    // Mostrar la imagen de la foto de perfil
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, Color.White, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } ?: run {
                                    // Mostrar un círculo azul claro en lugar de la foto de perfil si no está disponible
                                    Image(
                                        painter = painterResource(id = R.drawable.google_logo),
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier.size(80.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Nombre y correo electrónico del usuario
                                Column {
                                    Text(
                                        text = "${data["nombreUsuario"] ?: "Nombre no disponible"}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${data["email"] ?: "Correo no disponible"}", color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Editar perfil y Estadísticas
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(
                                    onClick = { navController.navigate("editarPerfil") },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = azul1
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(text = "Editar perfil", color = Color.White)
                                }

                                Button(
                                    onClick = { /* Navegar a la pantalla de estadísticas del usuario */ },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = azul1
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