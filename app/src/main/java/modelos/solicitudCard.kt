package modelos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.soundcore.ui.theme.backgroundOscuro
import com.google.firebase.auth.FirebaseAuth
import controladores.aceptarSolicitudDeAmistad
import controladores.rechazarSolicitudDeAmistad

@Composable
fun SolicitudCard(
    navController: NavController,
    nombreUsuario: String,
    uidRemitente: String,
    onSolicitudGestionada: () -> Unit
) {
    val contexto = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(backgroundOscuro),
    ) {
        Column(modifier = Modifier.padding(8.dp).background(backgroundOscuro)) {
            Text(
                text = nombreUsuario,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        aceptarSolicitudDeAmistad(uidRemitente, FirebaseAuth.getInstance().currentUser!!.uid) {
                            onSolicitudGestionada()
                        }
                        Toast.makeText(contexto, "Solicitud de amistad aceptada", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Aceptar", color = Color.White)
                }

                Button(
                    onClick = {
                        rechazarSolicitudDeAmistad(uidRemitente, FirebaseAuth.getInstance().currentUser!!.uid) {
                            onSolicitudGestionada()
                        }
                        Toast.makeText(contexto, "Solicitud de amistad rechazada", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Rechazar", color = Color.White)
                }
            }
        }
    }
}
