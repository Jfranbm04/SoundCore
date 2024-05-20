package controladores

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import androidx.navigation.NavController
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
                Toast.makeText(contexto, "Hubo un error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
        }
}

// Función para comprobar registro
fun comprobarRegistro(navController: NavController, contexto: Context, email: String, password: String){
    auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(contexto,"Te has registrado correctamente.",Toast.LENGTH_SHORT).show()
                navController.navigate(Paths.pantallaPrincipal.path) // Pasa a la pantalla principal

                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(contexto,"No se ha podido registrar correctamente.",Toast.LENGTH_SHORT).show()
            }
        }
}

