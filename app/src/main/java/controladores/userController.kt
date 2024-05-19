package controladores

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import android.content.Context

private lateinit var auth: FirebaseAuth





// Función login
fun comprobarLogin(contexto: Context, email: String, password: String){
    auth = FirebaseAuth.getInstance()
//    Log.i("Jorge guapo", "Log in with $email failed with reason ")
//    Toast.makeText(contexto, "hola", Toast.LENGTH_SHORT).show()


    // Iniciar sesión con Firebase
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Inicio de sesión exitoso
                Toast.makeText(contexto, "Sesión Iniciada", Toast.LENGTH_SHORT).show()
            } else {
                // Error en el inicio de sesión
//                Log.i("Jorge guapo", "Log in with $email failed with reason ${task.exception}")
                Toast.makeText(contexto, "Hubo un error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
        }
}

// Función para comprobar registro
fun comprobarRegistro(contexto: Context, email: String, password: String){
    auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(contexto,"Te has registrado correctamente.",Toast.LENGTH_SHORT).show()

                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(contexto,"No se ha podido registrar correctamente.",Toast.LENGTH_SHORT).show()
            }
        }
}

