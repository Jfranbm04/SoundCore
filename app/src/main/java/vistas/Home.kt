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




/*
Cuando se pulse el botón enviar palmada, vamos a añadir:
    - un registro en la tabla Palmadas con los campos UIDUsuario, nombreAudio (es el nombre del campo del tipo audiorecord_1717453296829.3gP que se almacena en firebase storage en la carpeta audios), puntuación.
    - un registro en la listaPalmadas con el UID de la palmada, sacada de la tabla Palmadas
*/
// Utilizo MediaCodec y TarsosDSP para el trato de audio / FFmpegKit
// JFDA: Revisar modal "Enviar solicitud" / "Ya sois amigos, ver perfil", pantalla editarPerfil.kt


@Composable
fun HomeScreen(navController: NavController) {
    var isPlaying by remember { mutableStateOf(false) }
    var isPlayingBack by remember { mutableStateOf(false) }
    var buttonColor by remember { mutableStateOf(azul1) }
    var progress by remember { mutableStateOf(0f) }
    var playbackProgress by remember { mutableStateOf(0f) }
    var showProgress by remember { mutableStateOf(false) }
    var showPlayback by remember { mutableStateOf(false) }
    var showPlaybackProgress by remember { mutableStateOf(false) }
    var audioScore by remember { mutableStateOf<Int?>(null) } // Estado para almacenar la puntuación
    val context = LocalContext.current
    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Pedir permisos
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission.value = isGranted
    }

    // Comprobar permisos
    LaunchedEffect(Unit) {
        if (!hasPermission.value) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // LaunchedEffect para manejar la grabación del audio y el progress indicator
    LaunchedEffect(isPlaying) {
        if (isPlaying && hasPermission.value) {
            showProgress = true
            startRecording(context)

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 3000) {
                progress = (System.currentTimeMillis() - startTime) / 3000f
                delay(50)
            }
            progress = 1f
            val score = stopRecording(context) // Obtener la puntuación después de detener la grabación
            audioScore = score // Actualizar el estado con la puntuación obtenida
            isPlaying = false
            buttonColor = azul1
            showProgress = false
            showPlayback = true
        }
    }

    // LaunchedEffect to manage the playback progress indicator
    LaunchedEffect(isPlayingBack) {
        if (isPlayingBack) {
            showPlaybackProgress = true
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 3000) {
                playbackProgress = (System.currentTimeMillis() - startTime) / 3000f
                delay(50)
            }
            playbackProgress = 1f
            isPlayingBack = false
            showPlaybackProgress = false
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
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
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
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
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(buttonColor)
                                    .clickable {
                                        if (!isPlaying) {
                                            isPlaying = true
                                            buttonColor = azul2
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
                                    if (isPlaying) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Pause",
                                            modifier = Modifier.size(50.dp),
                                            tint = buttonColor
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            modifier = Modifier.size(50.dp),
                                            tint = buttonColor
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            AnimatedVisibility(visible = showProgress) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    LinearProgressIndicator(
                                        progress = progress,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 32.dp),
                                        color = buttonColor
                                    )
                                    Text(
                                        text = "Grabando audio...",
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }

                            AnimatedVisibility(visible = showPlayback) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                        Button(
                                            onClick = {
                                                isPlayingBack = true
                                                playRecording(context) {
                                                    isPlayingBack = false
                                                }
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = azul1),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            Text(text = "Escucha tu palmada")
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                audioScore?.let { score ->
                                                    enviarPalmada(context, score)
                                                    Toast.makeText(context, "Palmada Enviada", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = azul2)
                                        ) {
                                            Text(text = "Enviar palmada")
                                        }
                                    }

                                    // Mostrar la puntuación de la palmada
                                    audioScore?.let {
                                        Text(
                                            text = "Puntuación de la palmada: $it",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(visible = showPlaybackProgress) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    LinearProgressIndicator(
                                        progress = playbackProgress,
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
                }
            )
        }
    )
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
