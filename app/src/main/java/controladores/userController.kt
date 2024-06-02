package controladores

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings.Global.getString
import androidx.navigation.NavController
import com.example.soundcore.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import modelos.Paths

private lateinit var auth: FirebaseAuth


// Función login
fun comprobarLogin(navController: NavController, contexto: Context, email: String, password: String){
    auth = FirebaseAuth.getInstance()

    // Iniciar sesión con Firebase
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Inicio de sesión exitoso
                Toast.makeText(contexto, "Sesión Iniciada", Toast.LENGTH_SHORT).show()
                navController.navigate(Paths.pantallaPrincipal.path) // Pasa a la pantalla principal
            } else {
                // Error en el inicio de sesión
                Toast.makeText(contexto, "Hubo un error al iniciar sesión. Inténtelo de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
}

//private const val TAG = "UserController"

// Función para comprobar registro
fun comprobarRegistro(navController: NavController, contexto: Context, nombreUsuario: String, email: String, contraseña: String, fotoPerfil: Uri?) {
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, contraseña)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Toast.makeText(contexto, "Te has registrado correctamente.", Toast.LENGTH_SHORT).show()
                navController.navigate(Paths.pantallaPrincipal.path) // Navegar a la pantalla principal

                // Subir foto de perfil y crear usuario en Firestore
                if (user != null) {
                    subirFotoPerfil(user.uid, fotoPerfil) { url ->
                        crearUsuarioFirestore(user.uid, nombreUsuario, email, url)
                    }
                }
            } else {
                Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                val errorMessage = task.exception?.message.toString()
                Toast.makeText(contexto, "Error al registrarse: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
}

// Función para subir la foto de perfil a Firebase Storage
private fun subirFotoPerfil(uid: String, fotoPerfil: Uri?, onSuccess: (String) -> Unit) {
    if (fotoPerfil == null) {
        onSuccess("") // No hay foto de perfil, continuar con URL vacía
        return
    }

    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("fotos_perfil/$uid.jpg")

    val uploadTask = storageRef.putFile(fotoPerfil)
    uploadTask.addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString())
        }
    }.addOnFailureListener { exception ->
        Log.e(TAG, "Error al subir la foto de perfil", exception)
        onSuccess("") // Continuar con URL vacía en caso de fallo
    }
}

// Función para descargar la imagen desde Firebase Storage y convertirla en un Bitmap
suspend fun descargarImagen(url: String): Bitmap? {
    return try {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        val fotoPerfilBytes = storageReference.getBytes(10 * 1024 * 1024).await() // Descargar hasta 10MB
        BitmapFactory.decodeByteArray(fotoPerfilBytes, 0, fotoPerfilBytes.size)
    } catch (e: Exception) {
        Log.e("PerfilScreen", "Error al descargar la imagen de perfil: $e")
        null
    }
}

// Añadir usuario a Firestore
private fun crearUsuarioFirestore(uid: String, nombreUsuario: String, email: String, fotoPerfilUrl: String) {
    val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("usuarios").document(uid)

    val userData = hashMapOf(
        "nombreUsuario" to nombreUsuario,
        "email" to email,
        "fotoPerfilUrl" to fotoPerfilUrl,
        "listaAmigos" to emptyList<String>()
    )

    docRef.set(userData)
        .addOnSuccessListener {
            Log.d(TAG, "Usuario creado en Firestore")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error al crear usuario en Firestore", e)
        }
}

// Sacar datos usuario firestore
suspend fun obtenerDatosUsuario(uid: String): Map<String, Any>? {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val document = firestore.collection("usuarios").document(uid).get().await()
        if (document.exists()) {
            document.data
        } else {
            Log.d("Firestore", "No hay un usuario con el uid: $uid")
            null
        }
    } catch (e: Exception) {
        Log.e("Firestore", "Error sacando los datos del usuario", e)
        null
    }
}

// Sacar imagen usuario storage
suspend fun obtenerUrlFotoPerfil(uid: String): String? {
    return try {
        val document = FirebaseFirestore.getInstance().collection("usuarios").document(uid).get().await()
        document.getString("fotoPerfilUrl")
    } catch (e: Exception) {
        Log.e("UserController", "Error al obtener la URL de la foto de perfil: $e")
        null
    }
}

// Obtener todos los usuarios desde Firestore
suspend fun obtenerTodosLosUsuarios(): List<Map<String, Any>> {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val documents = firestore.collection("usuarios").get().await()
        documents.documents.mapNotNull { it.data }
    } catch (e: Exception) {
        Log.e("UserController", "Error al obtener la lista de usuarios", e)
        emptyList()
    }
}

// Función para enviar solicitud de amistad
fun enviarSolicitudDeAmistad(uidRemitente: String, nombreUsuarioDestinatario: String) {
    val firestore = FirebaseFirestore.getInstance()

    // Buscar el UID del usuario destinatario por su nombre de usuario
    firestore.collection("usuarios")
        .whereEqualTo("nombreUsuario", nombreUsuarioDestinatario)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val uidDestinatario = documents.documents[0].id

                // Crear una nueva solicitud de amistad
                val solicitudData = hashMapOf(
                    "uidRemitente" to uidRemitente,
                    "uidDestinatario" to uidDestinatario
                )

                firestore.collection("solicitudes").add(solicitudData)
                    .addOnSuccessListener {
                        Log.d(TAG, "Solicitud de amistad enviada")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al enviar la solicitud de amistad", e)
                    }
            } else {
                Log.d(TAG, "No se encontró un usuario con ese nombre de usuario")
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error al buscar el usuario destinatario", e)
        }
}


// Obtener las solicitudes de amistad del usuario actual
suspend fun obtenerSolicitudesDeAmistad(uidDestinatario: String): List<Map<String, Any>> {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val documents = firestore.collection("solicitudes")
            .whereEqualTo("uidDestinatario", uidDestinatario)
            .get().await()
        documents.documents.mapNotNull { it.data }
    } catch (e: Exception) {
        Log.e("UserController", "Error al obtener las solicitudes de amistad", e)
        emptyList()
    }
}


// Función para aceptar solicitud de amistad
fun aceptarSolicitudDeAmistad(uidRemitente: String, uidDestinatario: String) {
    val firestore = FirebaseFirestore.getInstance()

    // Añadir el UID del destinatario a la lista de amigos del remitente
    val remitenteRef = firestore.collection("usuarios").document(uidRemitente)
    remitenteRef.update("listaAmigos", FieldValue.arrayUnion(uidDestinatario))
        .addOnSuccessListener {
            Log.d(TAG, "Amigo añadido a la lista del remitente")
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error al añadir amigo a la lista del remitente", e)
        }

    // Añadir el UID del remitente a la lista de amigos del destinatario
    val destinatarioRef = firestore.collection("usuarios").document(uidDestinatario)
    destinatarioRef.update("listaAmigos", FieldValue.arrayUnion(uidRemitente))
        .addOnSuccessListener {
            Log.d(TAG, "Amigo añadido a la lista del destinatario")
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error al añadir amigo a la lista del destinatario", e)
        }

    // Eliminar la solicitud de amistad
    firestore.collection("solicitudes")
        .whereEqualTo("uidRemitente", uidRemitente)
        .whereEqualTo("uidDestinatario", uidDestinatario)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                firestore.collection("solicitudes").document(document.id).delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Solicitud de amistad eliminada")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al eliminar la solicitud de amistad", e)
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error al buscar la solicitud de amistad", e)
        }
}

// Función para rechazar solicitud de amistad
fun rechazarSolicitudDeAmistad(uidRemitente: String, uidDestinatario: String) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("solicitudes")
        .whereEqualTo("uidRemitente", uidRemitente)
        .whereEqualTo("uidDestinatario", uidDestinatario)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                firestore.collection("solicitudes").document(document.id).delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Solicitud de amistad rechazada")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error al rechazar la solicitud de amistad", e)
                    }
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "Error al buscar la solicitud de amistad", e)
        }
}

// Obtener el nombre del usuario por UID
suspend fun obtenerNombreUsuario(uid: String): String? {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val document = firestore.collection("usuarios").document(uid).get().await()
        document.getString("nombreUsuario")
    } catch (e: Exception) {
        Log.e("UserController", "Error al obtener el nombre del usuario", e)
        null
    }
}

