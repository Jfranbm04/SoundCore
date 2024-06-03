package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import controladores.obtenerNombreUsuario
import controladores.obtenerSolicitudesDeAmistad
import kotlinx.coroutines.launch
import modelos.SolicitudCard

@Composable
fun SolicitudesScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val solicitudes = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            coroutineScope.launch {
                solicitudes.value = obtenerSolicitudesDeAmistad(uid)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes de Amistad", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold) },
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
                LazyColumn {
                    items(solicitudes.value) { solicitud ->
                        val uidRemitente = solicitud["uidRemitente"] as? String ?: ""

                        // Obtener el nombre del remitente
                        var nombreUsuario by remember { mutableStateOf("Cargando...") }
                        LaunchedEffect(uidRemitente) {
                            nombreUsuario = obtenerNombreUsuario(uidRemitente) ?: "Nombre no disponible"
                        }

                        // Mostrar la solicitud de amistad
                        SolicitudCard(navController, nombreUsuario, uidRemitente) {
                            coroutineScope.launch {
                                solicitudes.value = solicitudes.value.filter { it["uidRemitente"] != uidRemitente }
                            }
                        }
                    }
                }
            }
        }
    )
}


