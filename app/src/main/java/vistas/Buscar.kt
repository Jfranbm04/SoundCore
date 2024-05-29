package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip
import com.example.soundcore.ui.theme.backgroundClaro
import com.example.soundcore.ui.theme.backgroundOscuro

@Composable
fun BuscarScreen(navController: NavController) {
    val textFieldValue = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                backgroundColor = backgroundOscuro,
                contentColor = Color.White,
                elevation = 0.dp,
                modifier = Modifier.height(80.dp), // altura del TopAppBar
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "icono buscar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = { textFieldValue.value = it },
                        modifier = Modifier
                            .padding(end = 8.dp) // padding desde la derecha
                            .fillMaxWidth()
                            .height(50.dp), // Altura del TextField
//                            .clip(RoundedCornerShape(4.dp)),
                        placeholder = { Text("Buscar") },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = backgroundClaro,
                            textColor = Color.White,
                            placeholderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,

                            ),
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),


                    )
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
                Text(text = "Search Screen", color = Color.White)
            }
        }
    )
}