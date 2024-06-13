package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.azul2
import com.example.soundcore.ui.theme.backgroundOscuro
import controladores.obtenerTopPalmadasGlobales
import kotlinx.coroutines.launch
import modelos.AudioCard

@Composable
fun RankingGlobalScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var topPalmadas by remember { mutableStateOf<List<Map<String, Any>>?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            topPalmadas = obtenerTopPalmadasGlobales().take(10)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Top 10 Mundial",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                backgroundColor = azul2,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Salir", tint = Color.White)
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(azul2),
                contentAlignment = Alignment.TopCenter,
            ) {
                topPalmadas?.let { palmadas ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(palmadas) { palmada ->
                            AudioCard(audio = palmada)
                        }
                    }
                } ?: run {
                    CircularProgressIndicator(color = backgroundOscuro, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    )
}
