package vistas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
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
import androidx.navigation.NavController
import androidx.core.graphics.drawable.toBitmap
import com.example.soundcore.R
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.azul4
import controladores.comprobarRegistro
import modelos.Paths

@Composable
fun SignUpScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        HeaderSignUp(Modifier.align(Alignment.TopCenter), navController)
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun HeaderSignUp(modifier: Modifier, navController: NavController) {
    logoSignUp(navController)
}

@Composable
fun headerRegistro() {
    Text(
        text = "ÚNETE A LA ÉLITE",
        modifier = Modifier.fillMaxWidth(),
        fontSize = 30.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold,
        color = azul4
    )
}

@Composable
fun logoSignUp(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.soundcore_logo),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Start)
                .clickable {
                    navController.popBackStack()
                }
        )

        Spacer(modifier = Modifier.height(30.dp))
        headerRegistro()
        Spacer(modifier = Modifier.height(20.dp))
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
    var nombreUsuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var fotoPerfil by remember { mutableStateOf<Uri?>(null) }
    var aceptarProteccionDatos by remember { mutableStateOf(false) }
    var dialogRGPD by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    if (dialogRGPD) {
        AlertDialog(
            onDismissRequest = { dialogRGPD = false },
            title = { Text(text = "Política de Protección de Datos") },
            text = {
                Text(
                    text = "Política de Protección de Datos\n" +
                            "\n" +
                            "Nos comprometemos a proteger y respetar su privacidad. Esta política explica cuándo y por qué recopilamos información personal sobre las personas que visitan nuestra app, cómo la utilizamos, las condiciones bajo las cuales podemos divulgarla a otros y cómo la mantenemos segura.\n" +
                            "\n" +
                            "¿Qué información recopilamos?\n" +
                            "\n" +
                            "Recopilamos información sobre usted cuando se registra en nuestra app, añade datos personales o hace alguna operación dentro de ésta. La información recopilada puede incluir su nombre y dirección de correo electrónico."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    aceptarProteccionDatos = true
                    dialogRGPD = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogRGPD = false }) {
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
            nombreUsuario.isEmpty() -> mostrarToast("No has escrito un nombre de usuario.")
            fotoPerfil == null -> mostrarToast("No has seleccionado una foto de perfil.")
            else -> comprobarRegistro(navController, contexto, nombreUsuario, correo, contraseña, fotoPerfil)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fotoPerfil = uri
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
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
                    unfocusedIndicatorColor = Color.Transparent,
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
                    painterResource(id = R.drawable.visibility)
                } else {
                    painterResource(id = R.drawable.visibility_off)
                }
                Icon(
                    painter = icon,
                    contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = azul1
            )
        ) {
            Text(text = "Seleccionar foto de perfil", color = Color.White, fontSize = 16.sp)
        }

        fotoPerfil?.let {
            val context = LocalContext.current
            val imageBitmap = remember { mutableStateOf<android.graphics.Bitmap?>(null) }
            LaunchedEffect(it) {
                context.contentResolver.openInputStream(it)?.let { inputStream ->
                    imageBitmap.value = android.graphics.BitmapFactory.decodeStream(inputStream)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            imageBitmap.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
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
            Text(text = "Acepto la ", color = Color.White)
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
            Text(text = "Registrarme", color = Color.White, fontSize = 25.sp)
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

@Composable
fun VolverParaIniciarSesion(navController: NavController) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.White)) {
            append("¿Tienes una cuenta? ")
        }
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