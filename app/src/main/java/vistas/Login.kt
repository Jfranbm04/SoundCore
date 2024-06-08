package vistas

import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import controladores.comprobarLogin
import controladores.comprobarRegistro
import modelos.Paths

private lateinit var auth: FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color.Transparent)
    ) {
        Header(Modifier.align(Alignment.TopCenter), navController)
        Spacer(modifier = Modifier.height(50.dp))
        //Body(Modifier.align(Alignment.Center))
    }
}

@Composable
fun Header(modifier: Modifier, navController: NavController) {
    logo(navController)
}

@Composable
fun logo(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.soundcore_logo),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp) // Tamaño de la imagen
                .align(alignment = Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))
        headerLogin()
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
        correoContraseña(navController)
    }
}

@Composable
fun correoContraseña(navController: NavController) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLoginEnable by rememberSaveable { mutableStateOf(false) }

    Column {
        Email(email) {
            email = it
            if (password.isNotEmpty() && email.isNotEmpty() && isValidEmail(email))
                isLoginEnable = true
            else
                isLoginEnable = false
        }
        Spacer(modifier = Modifier.size(4.dp))
        Password(password) {
            password = it
            if (password.isNotEmpty() && email.isNotEmpty() && isValidEmail(email))
                isLoginEnable = true
            else
                isLoginEnable = false
        }
        Spacer(modifier = Modifier.size(32.dp))
        LoginButton(navController, email, password)

        Spacer(modifier = Modifier.size(32.dp))
        SignUpText(navController)
    }
}



// Funcion para registro (texto clickable)
@Composable
fun SignUpText(navController: NavController) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.White)) {
            append("¿No tienes cuenta? ")
        }
        pushStringAnnotation(tag = "signup", annotation = "signup")
        withStyle(style = SpanStyle(color = azul4, fontWeight = FontWeight.Bold)) {
            append("Crea una.")
        }
        pop()
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "signup", start = offset, end = offset)
                    .firstOrNull()?.let {
                        navController.navigate(route = Paths.signUp.path)
                    }
            }
        )
    }
}

// Función para mostrar el botón de login y darle funcionalidad
@Composable
fun LoginButton(navController: NavController, email: String, password: String) {
    val contexto = LocalContext.current

    Button(
        onClick = {
            comprobarLogin(navController, contexto, email, password)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContentColor = Color.White,
            containerColor = azul1,
        )
    ) {
        Text(text = "Iniciar Sesión", color = Color.White, fontSize = 25.sp)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Password(password: String, onTextChanged: (String) -> Unit) {
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(azul4)
    ) {
        TextField(
            value = password,
            onValueChange = { onTextChanged(it) },
            modifier = Modifier
                .weight(1f)
                .background(azul4),
            placeholder = { Text(text = "Contraseña") },
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
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Email(email: String, onTextChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onTextChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Email") },
        maxLines = 1,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.textFieldColors(
            // textColor = Color(0xFF000000),
            containerColor = azul4
        )
    )
}

@Composable
fun headerLogin() {
    Text(
        text = "INICIAR SESIÓN \n EN SOUNDCORE",

        modifier = Modifier
            .background(color = Color.Transparent)

            .fillMaxWidth(),
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold,
        color = azul4
    )
}

fun isValidEmail(email: String): Boolean {
    return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
}


