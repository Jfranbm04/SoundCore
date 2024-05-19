package vistas

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

        VolverParaIniciarSesion(navController)

    }
}

// Header registro
@Composable
fun headerRegistro() {
    Text(
        text = "REGÍSTRATE \n EN SOUNDCORE",

        modifier = Modifier
            .background(color = azul1)
            .border(1.dp, color = azul1)

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

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations( start = offset, end = offset)
                    .firstOrNull()?.let {
                        navController.popBackStack()
                    }
            }
        )
    }
}

