package controladores

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import android.widget.Toast
import java.io.IOException
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.MediaStore
import java.io.File
import kotlin.math.log10
import android.media.MediaMetadataRetriever
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


/*
Cuando se pulse el botón enviar palmada, vamos a añadir:
- un registro en la tabla Palmadas con los campos UIDUsuario, nombreAudio (es el nombre del campo del tipo audiorecord_1717453296829.3gP que se almacena en firebase storage en la carpeta audios), puntuación.
 - un registro en la listaPalmadas con el UID de la palmada, sacada de la tabla Palmadas
*/





// Variables globales para almacenar el archivo de salida y el nombre del archivo.

private val recorder = MediaRecorder()
private var outputFile: String = ""
private var mediaPlayer: MediaPlayer? = null
private lateinit var currentFileName: String
private val storageReference: StorageReference by lazy {
    FirebaseStorage.getInstance().reference
}

fun startRecording(context: Context) {
    val timestamp = System.currentTimeMillis()
    currentFileName = "audiorecord_$timestamp.3gp"

    outputFile = "${context.getExternalFilesDir(null)?.absolutePath}/$currentFileName"

    try {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)
            prepare()
            start()
        }
    } catch (e: IOException) {
        Log.e("AudioRecorderController", "prepare() failed")
        Toast.makeText(context, "Error al grabar audio", Toast.LENGTH_SHORT).show()
    }
}

suspend fun stopRecording(context: Context): Int {
    try {
        recorder.stop()
        recorder.reset()
        Toast.makeText(context, "Grabación exitosa", Toast.LENGTH_SHORT).show()
        uploadAudioToFirebase(context)

        val maxDecibel = withContext(Dispatchers.IO) {
            obtenerMaxDecibelio(context)
        }

        val normalizedDecibel = normalizeDecibel(maxDecibel)
        Log.d("AudioRecorderController", "Decibelio más alto (normalizado): $normalizedDecibel")

        return normalizedDecibel
    } catch (e: RuntimeException) {
        Log.e("AudioRecorderController", "stop() failed")
        Toast.makeText(context, "Error al detener la grabación", Toast.LENGTH_SHORT).show()
        return 0
    }
}

private fun uploadAudioToFirebase(context: Context) {
    val audioFile = Uri.fromFile(File(outputFile))
    val audioRef = storageReference.child("audios/$currentFileName")

    audioRef.putFile(audioFile)
        .addOnSuccessListener {
            Log.e("AudioRecorderController", "Audio subido a firebase")
        }
        .addOnFailureListener { e ->
            Log.e("AudioRecorderController", "Error al subir el audio a Firebase", e)
            Toast.makeText(context, "Error al subir el audio", Toast.LENGTH_SHORT).show()
        }
}

// Escuchar audio grabado
fun playRecording(context: Context, onCompletion: () -> Unit) {
    mediaPlayer = MediaPlayer().apply {
        try {
            setDataSource(outputFile)
            prepare()
            start()
            setOnCompletionListener {
                onCompletion()
            }
        } catch (e: IOException) {
            Log.e("AudioRecorderController", "play() failed")
            Toast.makeText(context, "Error al reproducir grabación", Toast.LENGTH_SHORT).show()
        }
    }
}

// Escuchar audio según nombre por parámetro
fun playStoredRecording(context: Context, fileName: String, onCompletion: () -> Unit) {
    val storageReference = FirebaseStorage.getInstance().reference.child("audios/$fileName")
    storageReference.downloadUrl.addOnSuccessListener { uri ->
        MediaPlayer().apply {
            try {
                setDataSource(context, uri)
                prepare()
                start()
                setOnCompletionListener {
                    onCompletion()
                }
            } catch (e: IOException) {
                Log.e("AudioRecorderController", "playStoredRecording() failed")
                Toast.makeText(context, "Error al reproducir grabación", Toast.LENGTH_SHORT).show()
            }
        }
    }.addOnFailureListener { e ->
        Log.e("AudioRecorderController", "Error al obtener URL de grabación", e)
        Toast.makeText(context, "Error al obtener URL de grabación", Toast.LENGTH_SHORT).show()
    }
}



fun stopPlaying() {
    mediaPlayer?.release()
    mediaPlayer = null
}



// Método que saca el decibelio máximo (sacado de internet)
suspend fun obtenerMaxDecibelio(context: Context): Double {
    val command = "-i $outputFile -filter:a volumedetect -f null /dev/null"

    val deferred = CompletableDeferred<Double>()

    FFmpegKit.executeAsync(command) { session ->
        val returnCode = session.returnCode
        if (ReturnCode.isSuccess(returnCode)) {
            // Procesar la salida para encontrar el volumen máximo
            val output = session.allLogsAsString
            val regex = Regex("max_volume: (-?\\d+\\.\\d+) dB")
            val match = regex.find(output)

            if (match != null) {
                val maxVolume = match.groupValues[1].toDouble()
                deferred.complete(maxVolume)
            } else {
                deferred.complete(Double.NaN) // Si no se encuentra el volumen máximo
            }
        } else {
            Log.e("FFmpeg", "Error ejecutando comando FFmpeg")
            deferred.completeExceptionally(RuntimeException("Error ejecutando comando FFmpeg"))
        }
    }

    return deferred.await()
}

// Define la función normalizeDecibel
fun normalizeDecibel(decibel: Double): Int {
//    Establezco los valores para hacer el rango de puntuación
    val minDb = -40.0
    val maxDb = 0.0
    val range = maxDb - minDb

    val normalized = ((decibel - minDb) * (100 - 0) / range) + 0

    return normalized.toInt().coerceIn(0, 100)
}


fun enviarPalmada(context: Context, puntuacion: Int) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = auth.currentUser

    if (user != null) {
        val uidUsuario = user.uid

        uploadAudioToFirebase(context) // Subir audio a firebase Storage

        // Crear un nuevo documento en la colección "Palmadas"
        val palmadasDocRef = firestore.collection("Palmadas").document()
        val uidPalmada = palmadasDocRef.id

        // Crear un objeto con los datos de la palmada
        val palmadaData = hashMapOf(
            "UIDUsuario" to uidUsuario,
            "nombreAudio" to currentFileName,
            "puntuacion" to puntuacion
        )

        // Añadir el objeto a la colección "Palmadas"
        palmadasDocRef.set(palmadaData)
            .addOnSuccessListener {
                Log.d("Firestore", "Puntuación añadida a la colección Palmadas")

                // Añadir el UID de la palmada a la listaPalmadas del usuario
                val userDocRef = firestore.collection("usuarios").document(uidUsuario)
                userDocRef.update("listaPalmadas", FieldValue.arrayUnion(uidPalmada))
                    .addOnSuccessListener {
                        Log.d("Firestore", "UID de la palmada añadida a listaPalmadas del usuario")
                        Toast.makeText(context, "Palmada Enviada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error al añadir UID de la palmada a listaPalmadas del usuario", e)
                        Toast.makeText(context, "Error al enviar la palmada", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al añadir la puntuación a la colección Palmadas", e)
                Toast.makeText(context, "Error al enviar la palmada", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
    }
}

suspend fun obtenerAudiosUsuario(uid: String): List<Map<String, Any>>? {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val usuarioDoc = firestore.collection("usuarios").document(uid).get().await()
        val listaPalmadas = usuarioDoc.get("listaPalmadas") as? List<String> ?: return emptyList()
        val audios = mutableListOf<Map<String, Any>>()
        for (palmadaId in listaPalmadas) {
            val palmadaDoc = firestore.collection("Palmadas").document(palmadaId).get().await()
            if (palmadaDoc.exists()) {
                audios.add(palmadaDoc.data!!)
            }
        }
        audios
    } catch (e: Exception) {
        Log.e("Firestore", "Error obteniendo audios del usuario", e)
        null
    }
}

// Función para ordenar los audios para el ranking
fun ordenarAudiosPorPuntuacion(audios: List<Map<String, Any>>): List<Map<String, Any>> {
    return audios.sortedByDescending { it["puntuacion"] as? Long ?: 0L }
}

// Función para obtener todos los audios de los amigos del usuario
suspend fun obtenerPalmadasAmigos(uid: String): List<Map<String, Any>>? {   // Los ordeno en una función aparte (la utilizaré más veces)
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val usuarioDoc = firestore.collection("usuarios").document(uid).get().await()
        val listaAmigos = usuarioDoc.get("listaAmigos") as? List<String> ?: return emptyList()
        val listaPalmadas = mutableListOf<String>()

        listaPalmadas.addAll(usuarioDoc.get("listaPalmadas") as? List<String> ?: emptyList())

        for (amigoId in listaAmigos) {
            val amigoDoc = firestore.collection("usuarios").document(amigoId).get().await()
            listaPalmadas.addAll(amigoDoc.get("listaPalmadas") as? List<String> ?: emptyList())
        }

        val audios = mutableListOf<Map<String, Any>>()
        for (palmadaId in listaPalmadas) {
            val palmadaDoc = firestore.collection("Palmadas").document(palmadaId).get().await()
            if (palmadaDoc.exists()) {
                audios.add(palmadaDoc.data!!)
            }
        }
        audios
    } catch (e: Exception) {
        Log.e("Firestore", "Error obteniendo palmadas de amigos", e)
        null
    }
}

suspend fun obtenerTopPalmadasGlobales(): List<Map<String, Any>> {
    val firestore = FirebaseFirestore.getInstance()
    return try {
        val palmadasQuery = firestore.collection("Palmadas")    // Consulta hecha desde aquí
            .orderBy("puntuacion", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        palmadasQuery.documents.mapNotNull { it.data }
    } catch (e: Exception) {
        Log.e("Firestore", "Error obteniendo las mejores palmadas globales", e)
        emptyList()
    }
}

// Función para eliminar todas las palmadas
suspend fun eliminarPalmadasUsuario(context: Context) {
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    if (user != null) {
        val uidUsuario = user.uid

        try {
            val usuarioDoc = firestore.collection("usuarios").document(uidUsuario).get().await()
            val listaPalmadas = usuarioDoc.get("listaPalmadas") as? List<String> ?: return

            // Eliminar cada palmada en Firestore y Firebase Storage
            listaPalmadas.forEach { palmadaId ->
                val palmadaDoc = firestore.collection("Palmadas").document(palmadaId).get().await()
                if (palmadaDoc.exists()) {
                    val nombreAudio = palmadaDoc.getString("nombreAudio")
                    if (!nombreAudio.isNullOrEmpty()) {
                        val audioRef = storage.reference.child("audios/$nombreAudio") // Se elimina de Storage
                        audioRef.delete().await()
                    }
                    firestore.collection("Palmadas").document(palmadaId).delete().await() // Se elimina de firestore
                }
            }

            // Actualizar el documento del usuario para eliminar la lista de palmadas
            firestore.collection("usuarios").document(uidUsuario).update("listaPalmadas", emptyList<String>()).await()

            Log.d("Firestore", "Se han eliminado todas las palmadas")
        } catch (e: Exception) {
            Log.e("Firestore", "Error eliminando palmadas del usuario", e)
            Toast.makeText(context, "Error al eliminar tus palmadas", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
    }
}


