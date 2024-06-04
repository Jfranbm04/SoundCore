package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import controladores.obtenerDatosUsuario
import kotlinx.coroutines.launch
import modelos.Paths
import modelos.UsuarioCard

@Composable
fun ListaAmigosScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var amigosList by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            coroutineScope.launch {
                val userData = obtenerDatosUsuario(uid)
                val listaAmigos = userData?.get("listaAmigos") as? List<String>
                if (listaAmigos != null) {
                    amigosList = listaAmigos.mapNotNull { amigoUid ->
                        obtenerDatosUsuario(amigoUid)
                    }
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
                title = {
                    Text(
                        "Tu lista de amigos",
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
                contentAlignment = Alignment.TopCenter,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(8.dp))
                } else {
                    amigosList?.let { amigos ->
                        LazyColumn {
                            items(amigos) { amigo ->
                                val nombreUsuario = amigo["nombreUsuario"] as? String ?: "Nombre no disponible"
                                val fotoPerfilUrl = amigo["fotoPerfilUrl"] as? String
                                UsuarioCard(nombreUsuario = nombreUsuario, fotoPerfilUrl = fotoPerfilUrl) {
                                    // Acción al hacer clic en el card del amigo (navegar a perfil del amigo, por ejemplo)
                                }
                            }
                        }
                    } ?: run {
                        Text(text = "No tienes amigos en tu lista", color = Color.White)
                    }
                }
            }
        }
    )
}