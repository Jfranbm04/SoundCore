package vistas

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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

import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import controladores.playRecording
import controladores.startRecording
import controladores.stopRecording
import controladores.evaluateRecording

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
    val context = LocalContext.current
    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Request permission launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission.value = isGranted
    }

    // Check for permission
    LaunchedEffect(Unit) {
        if (!hasPermission.value) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // LaunchedEffect to manage the progress indicator and audio recording
    LaunchedEffect(isPlaying) {
        if (isPlaying && hasPermission.value) {
            showProgress = true
            startRecording(context)

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 5000) {
                progress = (System.currentTimeMillis() - startTime) / 5000f
                delay(50)
            }
            progress = 1f
            stopRecording(context)
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
            while (System.currentTimeMillis() - startTime < 5000) {
                playbackProgress = (System.currentTimeMillis() - startTime) / 5000f
                delay(50)
            }
            playbackProgress = 1f
            isPlayingBack = false
            showPlaybackProgress = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
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
                    Button(
                        onClick = {
                            isPlayingBack = true
                            playRecording(context) {
                                isPlayingBack = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = azul1)
                    ) {
                        Text(text = "Escucha tu palmada")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val evaluation = evaluateRecording(context)
                            Toast.makeText(context, "Evaluación: $evaluation", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = azul1)
                    ) {
                        Text(text = "Evaluar palmada")
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
                        color = buttonColor
                    )
                    Text(
                        text = "Reproduciendo palmada...",
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}


