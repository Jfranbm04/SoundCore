package vistas

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
        MostrarRGPD(
            onDismissRequest = { dialogRGPD = false },
            onConfirm = {
                aceptarProteccionDatos = true
                dialogRGPD = false
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
fun MostrarRGPD(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(text = "Cerrar")
            }
        },
        title = {
            Text(text = "Protección de Datos", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text =
                    "POLÍTICA DE PRIVACIDAD PARA APLICACIONES MÓVILES (APPS)\n" +
                            "Fecha última actualización: 2024\n" +
                            "\n" +
                            "De conformidad con el Reglamento (UE) 2016/679, del Parlamento Europeo y del Consejo, de 27 de abril de 2016, relativo a la protección de las personas físicas en lo que respecta al tratamiento de datos personales y a la libre circulación de estos datos (Reglamento General de Protección de Datos – RGPD), SoundCore, informa a los usuarios de la aplicación [nombre de la aplicación] (en adelante, la Aplicación), acerca del tratamiento de los datos personales, que ellos voluntariamente hayan facilitado durante el proceso de registro, acceso y utilización del servicio.\n" +
                            "\n" +
                            "IDENTIFICACIÓN DEL RESPONSABLE DEL TRATAMIENTO\n" +
                            "SoundCore, con CIF/NIF n.º: [CIF/NIF] y domicilio a efectos de notificaciones en: [dirección] e inscrita en el Registro Mercantil de [ciudad] Tomo [número], Folio [número], Sección [sección], Hoja [número], inscripción [número] (en adelante, el Responsable del Tratamiento), es la entidad responsable del tratamiento de los datos facilitados por los clientes de la Aplicación (en adelante, el/los Usuario/s).\n" +
                            "\n" +
                            "FINALIDAD DEL TRATAMIENTO DE DATOS\n" +
                            "Para proceder al registro, acceso y posterior uso de la Aplicación, el Usuario deberá facilitar -de forma voluntaria-, datos de carácter personal (esencialmente, identificativos y de contacto), los cuales serán incorporados a soportes automatizados titularidad de SoundCore.\n" +
                            "\n" +
                            "La recogida, almacenamiento, modificación, estructuración y en su caso, eliminación, de los datos proporcionados por los Usuarios, constituirán operaciones de tratamiento llevadas a cabo por el Responsable, con la finalidad de garantizar el correcto funcionamiento de la Aplicación, mantener la relación de prestación de servicios y/o comercial con el Usuario, y para la gestión, administración, información, prestación y mejora del servicio.\n" +
                            "\n" +
                            "Los datos personales facilitados por el Usuario -especialmente, el correo electrónico o e-mail- podrán emplearse también para remitir boletines (newsletters), así como comunicaciones comerciales de promociones y/o publicidad de la Aplicación, siempre y cuando, el Usuario haya prestado previamente su consentimiento expreso para la recepción de estas comunicaciones vía electrónica.\n" +
                            "\n" +
                            "LEGITIMACIÓN\n" +
                            "El tratamiento de los datos del Usuario, se realiza con las siguientes bases jurídicas que legitiman el mismo:\n" +
                            "\n" +
                            "• La solicitud de información y/o la contratación de los servicios de la Aplicación, cuyos términos y condiciones se pondrán a disposición del Usuario en todo caso, con carácter previo, para su expresa aceptación.\n" +
                            "• El consentimiento libre, específico, informado e inequívoco del Usuario, poniendo a su disposición la presente política de privacidad, que deberá aceptar mediante una declaración o una clara acción afirmativa, como el marcado de una casilla dispuesta al efecto.\n" +
                            "\n" +
                            "En caso de que el Usuario no facilite a SoundCore sus datos, o lo haga de forma errónea o incompleta, no será posible proceder al uso de la Aplicación.\n" +
                            "\n" +
                            "CONSERVACIÓN DE LOS DATOS PERSONALES\n" +
                            "Los datos personales proporcionados por el Usuario, se conservarán en los sistemas y bases de datos del Responsable del Tratamiento, mientras aquel continúe haciendo uso de la Aplicación, y siempre que no solicite su supresión.\n" +
                            "\n" +
                            "Con el objetivo de depurar las posibles responsabilidades derivadas del tratamiento, los datos se conservarán por un período mínimo de cinco años.\n" +
                            "\n" +
                            "DESTINATARIOS\n" +
                            "Los datos no se comunicarán a ningún tercero ajeno a SoundCore, salvo obligación legal o en cualquier caso, previa solicitud del consentimiento del Usuario.\n" +
                            "\n" +
                            "De otra parte, SoundCore podrá dar acceso o transmitir los datos personales facilitados por el Usuario, a terceros proveedores de servicios, con los que haya suscrito acuerdos de encargo de tratamiento de datos, y que únicamente accedan a dicha información para prestar un servicio en favor y por cuenta del Responsable.\n" +
                            "\n" +
                            "RETENCIÓN DE DATOS\n" +
                            "SoundCore, informa al Usuario de que, como prestador de servicio de alojamiento de datos y en virtud de lo establecido en la Ley 34/2002, de 11 de julio, de Servicios de la Sociedad de la Información y de Comercio Electrónico (LSSI), retiene por un período máximo de 12 meses la información imprescindible para identificar el origen de los datos alojados y el momento en que se inició la prestación del servicio.\n" +
                            "\n" +
                            "La retención de estos datos no afecta al secreto de las comunicaciones y solo podrán ser utilizados en el marco de una investigación criminal o para la salvaguardia de la seguridad pública, poniéndose a disposición de los jueces y/o tribunales o del Ministerio que así los requiera.\n" +
                            "\n" +
                            "La comunicación de datos a las Fuerzas y Cuerpos de Seguridad del Estado, se hará en virtud de lo dispuesto por la normativa sobre protección de datos personales, y bajo el máximo respeto a la misma.\n" +
                            "\n" +
                            "PROTECCIÓN DE LA INFORMACIÓN ALOJADA\n" +
                            "El Responsable del Tratamiento, adopta las medidas necesarias para garantizar la seguridad, integridad y confidencialidad de los datos conforme a lo dispuesto en el Reglamento (UE) 2016/679 del Parlamento Europeo y del Consejo, de 27 de abril de\n" +
                            "2016, relativo a la protección de las personas físicas en lo que respecta al tratamiento de datos personales y a la libre circulación de los mismos.\n" +
                            "\n" +
                            "Si bien el Responsable, realiza copias de seguridad de los contenidos alojados en sus servidores, sin embargo, no se responsabiliza de la pérdida o el borrado accidental de los datos por parte de los Usuarios. De igual manera, no garantiza la reposición total de los datos borrados por los Usuarios, ya que los citados datos podrían haber sido suprimidos y/o modificados durante el período de tiempo transcurrido desde la última copia de seguridad.\n" +
                            "\n" +
                            "Los servicios facilitados o prestados a través de la Aplicación, excepto los servicios específicos de backup, no incluyen la reposición de los contenidos conservados en las copias de seguridad realizadas por el Responsable del Tratamiento, cuando esta pérdida sea imputable al usuario; en este caso, se determinará una tarifa acorde a la complejidad y volumen de la recuperación, siempre previa"
                )


            }
        }
    )
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