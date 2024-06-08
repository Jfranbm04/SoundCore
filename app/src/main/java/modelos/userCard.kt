package modelos

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.soundcore.R
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.backgroundOscuro
import controladores.descargarImagen

@Composable
fun UsuarioCard(nombreUsuario: String, fotoPerfilUrl: String?, onClick: () -> Unit) {
    val bitmapState = produceState<Bitmap?>(initialValue = null, fotoPerfilUrl) {
        value = fotoPerfilUrl?.let { descargarImagen(it) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Transparent)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(backgroundOscuro)
                .fillMaxWidth()
        ) {
            if (bitmapState.value != null) {
                Image(
                    bitmap = bitmapState.value!!.asImageBitmap(),
                    contentDescription = "Foto de perfil de $nombreUsuario",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop // Ajusta la imagen al contenedor (Se recorta la imagen si es necesario)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Foto de perfil por defecto",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),

                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = nombreUsuario, color = Color.White)
        }
    }
}