package vistas

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.azul4
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import controladores.descargarImagen
import controladores.obtenerDatosUsuario
import controladores.subirFotoPerfil
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import modelos.Paths

@Composable
fun EditarPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val uid = user?.uid

    var nombreUsuario by remember { mutableStateOf(TextFieldValue("")) }
    var fotoPerfilBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var newFotoPerfilUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Cargar datos del usuario actual
    LaunchedEffect(uid) {
        if (uid != null) {
            coroutineScope.launch {
                val userData = obtenerDatosUsuario(uid)
                if (userData != null) {
                    nombreUsuario = TextFieldValue(userData["nombreUsuario"] as String)
                    val fotoUrl = userData["fotoPerfilUrl"] as String?
                    if (!fotoUrl.isNullOrEmpty()) {
                        fotoPerfilBitmap = descargarImagen(fotoUrl)
                    }
                }
            }
        }
    }

    // Selector de imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        newFotoPerfilUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = backgroundOscuro,
                contentColor = backgroundOscuro
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundOscuro),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Imagen de perfil
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { launcher.launch("image/*") }
                ) {
                    newFotoPerfilUri?.let {
                        val inputStream = context.contentResolver.openInputStream(it)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        fotoPerfilBitmap = bitmap
                    }
                    fotoPerfilBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo de texto para el nombre de usuario
                TextField(
                    value = nombreUsuario,
                    onValueChange = { nombreUsuario = it },
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth().padding(16.dp)
                        .background(azul4)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botón de guardar
                androidx.compose.material3.Button(
                    onClick = {
                        if (uid != null) {
                            coroutineScope.launch {
                                val userRef = db.collection("usuarios").document(uid)
                                val updateData = mutableMapOf<String, Any>()
                                updateData["nombreUsuario"] = nombreUsuario.text

                                newFotoPerfilUri?.let { uri ->
                                    subirFotoPerfil(uid, uri) { url ->
                                        updateData["fotoPerfilUrl"] = url
                                        userRef.update(updateData).addOnSuccessListener {
                                            navController.popBackStack()
                                        }
                                    }
                                } ?: run {
                                    userRef.update(updateData).addOnSuccessListener {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = azul1
                    )
                ) {
                    Text("Guardar cambios")
                }
            }
        }
    )
}
