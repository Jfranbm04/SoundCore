package modelos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.soundcore.ui.theme.azul2
import com.example.soundcore.ui.theme.backgroundOscuro
import controladores.playStoredRecording
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AudioCard(audio: Map<String, Any>) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val progressAnimationDuration = 3000L // 3 segundos

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundOscuro),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            IconButton(onClick = {
                if (!isPlaying) {
                    isPlaying = true
                    coroutineScope.launch {
                        playStoredRecording(context, audio["nombreAudio"] as String) {
                            isPlaying = false
                            progress = 0f
                        }
                    }
                } else {
                    isPlaying = false
                    progress = 0f
                }
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Detener" else "Reproducir",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            LaunchedEffect(isPlaying) {
                if (isPlaying) {
                    val startTime = System.currentTimeMillis()
                    while (isPlaying && System.currentTimeMillis() - startTime < progressAnimationDuration) {
                        progress = (System.currentTimeMillis() - startTime).toFloat() / progressAnimationDuration
                        delay(16)
                    }
                    progress = 1f
                } else {
                    progress = 0f
                }
            }

            LinearProgressIndicator(
                progress = progress,
                color = azul2,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(4.dp)
            )

            Text(
                text = "${audio["puntuacion"] ?: 0}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(azul2, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
