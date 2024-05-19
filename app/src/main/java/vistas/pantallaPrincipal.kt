package vistas

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.soundcore.R
import com.example.soundcore.ui.theme.SoundCoreTheme
import com.example.soundcore.ui.theme.backgroundOscuro



@Composable
fun MainScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundOscuro)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.soundcore_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .clip(shape = MaterialTheme.shapes.medium)
            )
        }
    }
}

// Método para añadir toast
fun showToast(context: Context, mensaje: String, duracion: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, mensaje, duracion).show()
}

