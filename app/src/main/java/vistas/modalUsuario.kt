package vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.soundcore.ui.theme.azul1
import com.example.soundcore.ui.theme.backgroundClaro
import com.example.soundcore.ui.theme.backgroundOscuro
import controladores.enviarSolicitudDeAmistad

// Modal para mandar solicitud de amistad desde pantalla Buscar
@Composable
fun ModalContent(nombreUsuario: String, uidRemitente: String, solicitudEnviada: Boolean, onSolicitudEnviadaChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(backgroundOscuro)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Perfil de $nombreUsuario",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (solicitudEnviada) {
                Text(
                    text = "Solicitud enviada",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundClaro)
                        .padding(vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Button(
                    onClick = {
                        enviarSolicitudDeAmistad(uidRemitente, nombreUsuario)
                        onSolicitudEnviadaChange(true)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = azul1
                    )
                ) {
                    Text(
                        text = "Enviar solicitud de amistad",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            }
        }
    }
}

