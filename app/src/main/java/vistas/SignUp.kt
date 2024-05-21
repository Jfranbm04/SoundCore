package vistas

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.PatternsCompat
import androidx.navigation.NavController
import com.example.soundcore.R
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.azul2
import com.example.soundcore.ui.theme.azul3
import com.example.soundcore.ui.theme.azul4

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import controladores.comprobarRegistro
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import modelos.Paths

private lateinit var auth: FirebaseAuth

@Composable
fun SignUpScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        HeaderSignUp(Modifier.align(Alignment.TopCenter), navController)
        Spacer(modifier = Modifier.height(50.dp))
        //Body(Modifier.align(Alignment.Center))
    }
}

@Composable
fun HeaderSignUp(modifier: Modifier,navController: NavController) {
    logoSignUp(navController)
}

@Composable
fun logoSignUp(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.soundcore_logo),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp) // Tamaño de la imagen
                .align(alignment = Alignment.Start)
                .clickable {
                    navController.popBackStack()
                }
        )

        Spacer(modifier = Modifier.height(30.dp))
        headerRegistro()
        Spacer(modifier = Modifier.height(20.dp))
        // Divider con padding
        Divider(
            color = Color.Gray,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .height(0.5.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        camposRegistro(navController)

        Spacer(modifier = Modifier.height(20.dp))
        VolverParaIniciarSesion(navController)


    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun camposRegistro(navController: NavController) {
    val contexto = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var aceptarProteccionDatos by remember { mutableStateOf(false) }
    var dialogRGPD by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by rememberSaveable { mutableStateOf(false) }


    // AlertDialog para la protección de datos
    if (dialogRGPD) {
        AlertDialog(
            onDismissRequest = { dialogRGPD = false },
            title = { Text(text = "Política de Protección de Datos") },
            text = { Text(text = "Política de Protección de Datos\n" +
                    "\n" +
                    "Nos comprometemos a proteger y respetar su privacidad. Esta política explica cuándo y por qué recopilamos información personal sobre las personas que visitan nuestra app, cómo la utilizamos, las condiciones bajo las cuales podemos divulgarla a otros y cómo la mantenemos segura.\n" +
                    "\n" +
                    "¿Qué información recopilamos?\n" +
                    "\n" +
                    "Recopilamos información sobre usted cuando se registra en nuestra app, añade datos personales o hace alguna operación dentro de ésta. La información recopilada puede incluir su nombre y dirección de correo electrónico.") },
            confirmButton = {
                TextButton(onClick = {
                    aceptarProteccionDatos = true
                    dialogRGPD = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogRGPD = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    fun mostrarToast(mensaje: String) {
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
    }

    fun comprobarRegistro() {
        when {
            !aceptarProteccionDatos -> mostrarToast("Debes aceptar la protección de datos.")
            correo.isEmpty() -> mostrarToast("No has proporcionado un correo.")
            contraseña.isEmpty() -> mostrarToast("No has escrito una contraseña.")
            else -> comprobarRegistro(navController, contexto, correo, contraseña)
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = azul4,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = azul4,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(azul4)
        ) {
            TextField(
                value = contraseña,
                onValueChange = { contraseña = it },
                modifier = Modifier
                    .weight(1f)
                    .background(azul4),
                label = { Text("Contraseña") },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = azul4,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                visualTransformation = if (showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                }
            )
            IconButton(
                onClick = { showPassword = !showPassword },
                modifier = Modifier.background(azul4)

            ) {
                val icon: Painter = if (showPassword) {
                    painterResource(id = R.drawable.visibility) // ojo abierto
                } else {
                    painterResource(id = R.drawable.visibility_off) // ojo cerrado
                }
                Icon(
                    painter = icon,
                    contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptarProteccionDatos,
                onCheckedChange = { aceptarProteccionDatos = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = azul1,
                    uncheckedColor = azul4
                )

            )
            Text(text = "Acepto la ", color = Color.Black)
            Text(
                text = "protección de datos",
                color = azul4,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.clickable { dialogRGPD = true }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { comprobarRegistro() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContentColor = Color.White,
                containerColor = azul1
            )
        ) {
            Text(text= "Registrarme", color = azul4, fontSize = 25.sp)
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = azul4,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}



// Header registro
@Composable
fun headerRegistro() {
    Text(
        text = "REGÍSTRATE \n EN SOUNDCORE",

        modifier = Modifier
            .fillMaxWidth(),
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold,
        color = azul4
    )
}

// Función para hacer el texto clickable
@Composable
fun VolverParaIniciarSesion(navController: NavController) {
    val annotatedString = buildAnnotatedString {
        append("¿Tienes una cuenta? ")
        pushStringAnnotation(tag = "login", annotation = "login")
        withStyle(style = SpanStyle(color = azul4, fontWeight = FontWeight.Bold)) {
            append("Inicia sesión.")
        }
        pop()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(start = offset, end = offset)
                    .firstOrNull()?.let {
                        navController.popBackStack()
                    }
            }
        )
    }
}


