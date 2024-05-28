package vistas

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import modelos.Paths

@Composable
fun editarPerfilScreen(navController: NavController) {
    // Estado para almacenar el nombre del usuario y la URL de la foto de perfil
    var nombreUsuario by remember { mutableStateOf("") }
    var fotoPerfilUrl by remember { mutableStateOf("") }

    // Obtener el usuario actual desde Firestore
    val currentUser = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            try {
                val document = firestore.collection("usuarios").document(uid).get().await()
                nombreUsuario = document.getString("nombreUsuario") ?: ""
                fotoPerfilUrl = document.getString("fotoPerfilUrl") ?: ""
            } catch (e: Exception) {
                Log.e("editarPerfilScreen", "Error al obtener datos del usuario: $e")
            }
        }
    }

    Scaffold(
        topBar = {
            // Código omitido para mayor claridad
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundOscuro),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Mostrar la foto de perfil
//                    Image(
//                        bitmap = bitmap.asImageBitmap(),
//                        contentDescription = "Foto de perfil",
//                        modifier = Modifier
//                            .size(80.dp)
//                            .clip(CircleShape)
//                            .border(2.dp, Color.White, CircleShape),
//                        contentScale = ContentScale.Crop
//                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // TextField para editar el nombre de usuario
                    OutlinedTextField(
                        value = nombreUsuario,
                        onValueChange = { nombreUsuario = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para guardar los cambios
                    Button(
                        onClick = {
                            // Guardar los cambios en Firestore
//                            currentUser?.uid?.let { uid ->
//                                firestore.collection("usuarios").document(uid)
//                                    .update(mapOf("nombreUsuario" to nombreUsuario))
//                                    .addOnSuccessListener {
//                                        // Éxito al actualizar los datos del usuario
//                                        Toast.makeText(
//                                            LocalContext.current,
//                                            "Datos actualizados exitosamente",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                    .addOnFailureListener { e ->
//                                        // Error al actualizar los datos del usuario
//                                        Log.e("editarPerfilScreen", "Error al actualizar datos del usuario: $e")
//                                        Toast.makeText(
//                                            LocalContext.current,
//                                            "Error al actualizar datos del usuario",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cambios")
                    }
                }
            }
        }
    )
}