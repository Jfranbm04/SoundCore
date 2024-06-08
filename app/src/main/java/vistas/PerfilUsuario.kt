package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.backgroundOscuro
import controladores.obtenerDatosUsuario
import kotlinx.coroutines.launch

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import controladores.eliminarAmigo
import controladores.enviarSolicitudDeAmistad
import controladores.obtenerAudiosUsuario
import controladores.sonAmigos

import modelos.AudioCard
import modelos.Paths






@Composable
fun PerfilUsuarioScreen(navController: NavController, userId: String) {
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val usuario = remember { mutableStateOf<Map<String, Any>?>(null) }
    val esAmigo = remember { mutableStateOf<Boolean?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var fotoPerfilBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var audiosList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val dialogMessage = "¿Estás seguro de que quieres eliminar a este amigo?"
    var needsUpdate by remember { mutableStateOf(false) }
    var solicitudEnviada by remember { mutableStateOf(false) }

    LaunchedEffect(needsUpdate) {
        coroutineScope.launch {
            usuario.value = obtenerDatosUsuario(userId)
            usuario.value?.get("fotoPerfilUrl")?.let { url ->
                fotoPerfilBitmap = descargarImagen(url as String)
            }
            audiosList = obtenerAudiosUsuario(userId)
            esAmigo.value = sonAmigos(currentUser, userId)
            needsUpdate = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = usuario.value?.get("nombreUsuario") as? String ?: "Perfil",
                        color = Color.White
                    )
                },
                backgroundColor = backgroundOscuro,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
                usuario.value?.let { usuario ->
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            fotoPerfilBitmap?.let { bitmap ->
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
                                Image(
                                    painter = painterResource(id = R.drawable.google_logo),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.size(80.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = "${usuario["nombreUsuario"] ?: "Nombre no disponible"}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${usuario["email"] ?: "Correo no disponible"}", color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        esAmigo.value?.let { amigo ->
                            if (amigo) {
                                Button(
                                    onClick = { showDialog = true },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(text = "Eliminar amigo", color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(80.dp))

                                Divider(color = backgroundClaro, modifier = Modifier.fillMaxWidth().height(5.dp))

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Palmadas",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                audiosList?.let { audios ->
                                    LazyColumn {
                                        items(audios) { audio ->
                                            AudioCard(audio = audio)
                                        }
                                    }
                                }
                            } else {
                                if (solicitudEnviada) {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                            .background(backgroundClaro, shape = RoundedCornerShape(4.dp))
                                            .padding(vertical = 10.dp,horizontal = 16.dp)
                                    ) {
                                        Text(
                                            text = "Solicitud enviada",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            enviarSolicitudDeAmistad(currentUser, usuario["nombreUsuario"] as String)
                                            solicitudEnviada = true
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = azul1
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Text(text = "Enviar solicitud de amistad", color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(80.dp))

                                Divider(color = backgroundClaro, modifier = Modifier.fillMaxWidth().height(5.dp))

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Palmadas",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Solo los amigos pueden ver las palmadas",
                                    color = Color.Gray,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        } ?: run {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    }
                } ?: run {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(8.dp))
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "¿Estás seguro?") },
                    text = { Text(text = dialogMessage) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                eliminarAmigo(currentUser, userId)
                                needsUpdate = true
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                        ) {
                            Text("Eliminar", color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.buttonColors(backgroundColor = azul1)
                        ) {
                            Text("Cancelar", color = Color.White)
                        }
                    }
                )
            }
        }
    )
}