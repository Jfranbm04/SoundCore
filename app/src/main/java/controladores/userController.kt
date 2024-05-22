package controladores

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.provider.Settings.Global.getString
import androidx.navigation.NavController
import com.example.soundcore.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
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

// Función para comprobar registro
fun comprobarRegistro(navController: NavController, contexto: Context, nombreUsuario : String, email: String, contraseña: String){
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, contraseña)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Toast.makeText(contexto, "Te has registrado correctamente.", Toast.LENGTH_SHORT).show()
                navController.navigate(Paths.pantallaPrincipal.path) // Navigate to main screen

                // Añadir usuario a firestore
                crearUsuarioFirestore(user!!.uid, nombreUsuario, email)
            } else {
                Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                val errorMessage = task.exception!!.message.toString()
                Toast.makeText(contexto, "Error al registrarse: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
}

// Añadir usuario a firestore
private fun crearUsuarioFirestore(uid: String, nombreUsuario: String, email: String) {
    val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("usuarios").document(uid)

    val userData = hashMapOf(
        "nombreUsuario" to nombreUsuario,
        "email" to email,
        "contraseña" to ""
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
