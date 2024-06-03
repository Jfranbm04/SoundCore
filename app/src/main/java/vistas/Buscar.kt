package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.example.soundcore.ui.theme.backgroundClaro
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import controladores.obtenerTodosLosUsuarios
import kotlinx.coroutines.launch
import modelos.UsuarioCard

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BuscarScreen(navController: NavController) {
    val textFieldValue = remember { mutableStateOf("") }
    val usuarios = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val usuariosFiltrados = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()

    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val selectedUsuario = remember { mutableStateOf<Map<String, Any>?>(null) }
    var solicitudEnviada by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            usuarios.value = obtenerTodosLosUsuarios()
            usuariosFiltrados.value = usuarios.value
        }
    }

    // fiLTRA
    LaunchedEffect(textFieldValue.value) {
        usuariosFiltrados.value = if (textFieldValue.value.isEmpty()) {
            usuarios.value
        } else {
            usuarios.value.filter {
                val nombreUsuario = it["nombreUsuario"] as? String ?: ""
                nombreUsuario.contains(textFieldValue.value, ignoreCase = true)
            }
        }
    }

    val uidRemitente = auth.currentUser?.uid ?: ""

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            selectedUsuario.value?.let { usuario ->
                val nombreUsuario = usuario["nombreUsuario"] as? String ?: "Sin nombre"
                ModalContent(nombreUsuario, uidRemitente, solicitudEnviada, { solicitudEnviada = it })
            }
        }
    ) {
        // Al cerrar el modal se restaura el botón Enviar Solicitud
        LaunchedEffect(modalBottomSheetState.currentValue) {
            if (modalBottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                solicitudEnviada = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    backgroundColor = backgroundOscuro,
                    contentColor = Color.White,
                    elevation = 0.dp,
                    modifier = Modifier.height(80.dp),
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "icono buscar",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        TextField(
                            value = textFieldValue.value,
                            onValueChange = { textFieldValue.value = it },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .fillMaxWidth()
                                .height(50.dp),
                            placeholder = { Text("Buscar") },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = backgroundClaro,
                                textColor = Color.White,
                                placeholderColor = Color.White.copy(alpha = 0.7f),
                                cursorColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                        )
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundOscuro)
                        .padding(paddingValues)
                ) {
                    LazyColumn {
                        items(usuariosFiltrados.value) { usuario ->
                            val nombreUsuario = usuario["nombreUsuario"] as? String ?: "Sin nombre"
                            val fotoPerfilUrl = usuario["fotoPerfilUrl"] as? String
                            UsuarioCard(
                                nombreUsuario = nombreUsuario,
                                fotoPerfilUrl = fotoPerfilUrl,
                                onClick = {
                                    selectedUsuario.value = usuario
                                    coroutineScope.launch {
                                        modalBottomSheetState.show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}
