package vistas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.azul2
import kotlinx.coroutines.delay

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.soundcore.R
import com.example.soundcore.ui.theme.backgroundOscuro
import controladores.enviarPalmada
import controladores.playRecording
import controladores.startRecording
import controladores.stopRecording
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import java.util.Calendar
import java.util.concurrent.TimeUnit


/*
Cuando se pulse el botón enviar palmada, vamos a añadir:
    - un registro en la tabla Palmadas con los campos UIDUsuario, nombreAudio (es el nombre del campo del tipo audiorecord_1717453296829.3gP que se almacena en firebase storage en la carpeta audios), puntuación.
    - un registro en la listaPalmadas con el UID de la palmada, sacada de la tabla Palmadas
*/
// Utilizo FFmpegKit para el trato de audio / MediaCodec y TarsosDSP



@Composable
fun HomeScreen(navController: NavController) {
    var estaGrabando by remember { mutableStateOf(false) }
    var estaReproduciendo by remember { mutableStateOf(false) }
    var colorBoton by remember { mutableStateOf(azul1) }
    var progreso by remember { mutableStateOf(0f) }
    var progresoReproduccion by remember { mutableStateOf(0f) }
    var mostrarProgreso by remember { mutableStateOf(false) }
    var mostrarReproduccion by remember { mutableStateOf(false) }
    var mostrarProgresoReproduccion by remember { mutableStateOf(false) }
    var puntuacionAudio by remember { mutableStateOf<Int?>(null) }
    var aplausoEnviado by remember { mutableStateOf(false) }
    var mostrarTutorial by remember { mutableStateOf(false) }
    var pasoTutorial by remember { mutableStateOf(0) }
    val contexto = LocalContext.current
    val tienePermiso = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val estadoDrawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
        tienePermiso.value = concedido
    }

    LaunchedEffect(Unit) {
        if (!tienePermiso.value) {
            lanzadorPermiso.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(estaGrabando) {
        if (estaGrabando && tienePermiso.value) {
            mostrarProgreso = true
            mostrarReproduccion = false
            aplausoEnviado = false
            startRecording(contexto)

            val tiempoInicio = System.currentTimeMillis()
            while (System.currentTimeMillis() - tiempoInicio < 3000) {
                progreso = (System.currentTimeMillis() - tiempoInicio) / 3000f
                delay(50)
            }
            progreso = 1f
            val puntuacion = stopRecording(contexto)
            puntuacionAudio = puntuacion
            estaGrabando = false
            colorBoton = azul1
            mostrarProgreso = false
            mostrarReproduccion = true
        }
    }

    LaunchedEffect(estaReproduciendo) {
        if (estaReproduciendo) {
            mostrarProgresoReproduccion = true
            val tiempoInicio = System.currentTimeMillis()
            while (System.currentTimeMillis() - tiempoInicio < 3000) {
                progresoReproduccion = (System.currentTimeMillis() - tiempoInicio) / 3000f
                delay(50)
            }
            progresoReproduccion = 1f
            estaReproduciendo = false
            mostrarProgresoReproduccion = false
        }
    }

    ModalNavigationDrawer(
        drawerState = estadoDrawer,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 40.dp)
                    .background(backgroundOscuro)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(backgroundOscuro),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.soundcore_banner),
                            contentDescription = "Banner soundCore",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(bottomEnd = 8.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "UNLEASH THE POWER OF YOUR CLAPS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Divider(color = Color.White, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                DrawerItem("Ranking local") { navController.navigate("rankingLocal") }
                DrawerItem("Ranking con amigos") { navController.navigate("rankingAmigos") }
                DrawerItem("Ranking global") { navController.navigate("rankingGlobal") }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.soundcore_logo),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)

                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SoundCore", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        backgroundColor = backgroundOscuro,
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { estadoDrawer.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = { mostrarTutorial = true }) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Tutorial",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                )
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundOscuro)
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(colorBoton)
                                    .clickable {
                                        if (!estaGrabando) {
                                            estaGrabando = true
                                            colorBoton = azul2
                                            mostrarReproduccion = false
                                            mostrarProgreso = true
                                            puntuacionAudio = null
                                        }
                                    }
                                    .padding(16.dp)
                                    .border(4.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (estaGrabando) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Pause",
                                            modifier = Modifier.size(50.dp),
                                            tint = colorBoton
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            modifier = Modifier.size(50.dp),
                                            tint = colorBoton
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            AnimatedVisibility(visible = mostrarProgreso) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    LinearProgressIndicator(
                                        progress = progreso,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 32.dp),
                                        color = colorBoton
                                    )
                                    Text(
                                        text = "Grabando audio...",
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = mostrarReproduccion) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Button(
                                            onClick = {
                                                estaReproduciendo = true
                                                playRecording(contexto) {
                                                    estaReproduciendo = false
                                                }
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = azul1),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Text(text = "Escucha tu palmada")
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        if (aplausoEnviado) {
                                            Text(
                                                text = "Palmada subida",
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                                            )
                                        } else {
                                            Button(
                                                onClick = {
                                                    puntuacionAudio?.let { puntuacion ->
                                                        enviarPalmada(contexto, puntuacion)
                                                        aplausoEnviado = true
                                                    }
                                                },
                                                shape = RoundedCornerShape(4.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = azul2)
                                            ) {
                                                Text(text = "Subir palmada")
                                            }
                                        }
                                    }

                                    puntuacionAudio?.let {
                                        Text(
                                            text = "Puntuación de la palmada: $it",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(visible = mostrarProgresoReproduccion) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    LinearProgressIndicator(
                                        progress = progresoReproduccion,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 32.dp),
                                        color = azul1
                                    )
                                    Text(
                                        text = "Reproduciendo audio...",
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (mostrarTutorial) {
                        AlertDialog(
                            onDismissRequest = {
                                pasoTutorial = 0
                                mostrarTutorial = false
                            },
                            title = {
                                Text(text = "Aprende a utilizar SoundCore", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            },
                            text = {
                                when (pasoTutorial) {
                                    0 -> PasoTutorial("Bienvenido al tutorial de SoundCore. \n Para un correcto funcionamiento de la app asegúrate de grabar las palmadas a más de 30cm del dispositivo. \n En la pantalla principal verás un botón principal, donde al pulsar se grabará un audio de 3 segundos donde deberás hacer una palmada con tu amigo. \n Una vez hayas terminado se mostrará la puntuación, junto con un botón para escuchar tu palmada y otro para subir la palmada a la red. \n Además podrás acceder al menú lateral donde están los rankings y ver tus mejores marcas, competir con tus amigos o con el mundo entero.",
                                        R.drawable.tuto01)
                                    1 -> PasoTutorial("¡Busca a tus amigos en la red! \n Puedes utilizar el buscador para encontrar usuarios y mandarles solicitudes de amistad para empezar a interactuar con ellos.", R.drawable.tuto02)
                                    2 -> PasoTutorial("¡Interactúa con los demás! \n Agrega a tus amigos y accede a escuchar sus audios, ver sus puntuaciones y participar en el ranking con ellos.", R.drawable.tuto03)
                                    3 -> PasoTutorial("Tu perfil. \n En este apartado puedes ver toda tu información. Tu colección de palmadas, tus puntuaciones, tus datos, tus amigos etc. También puedes modificar tu nombre de usuario y tu foto de perfil, aceptar o rechazar solicitudes de amistad y acceder a otras opciones desde el apartado de ajustes.", R.drawable.tuto04)
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        if (pasoTutorial < 3) {
                                            pasoTutorial += 1
                                        } else {
                                            pasoTutorial = 0
                                            mostrarTutorial = false
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (pasoTutorial < 3) "Siguiente" else "Cerrar",
                                        color = Color.Blue
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun PasoTutorial(descripcion: String, imagenRes: Int) {
    Column {
        Text(descripcion)
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = imagenRes),
            contentDescription = "Imagen del tutorial",
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(8.dp),
            contentScale = ContentScale.Inside
        )
    }
}
@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}