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

    val normalized = ((decibel - minDb) * (100 - 1) / range) + 1

    return normalized.toInt().coerceIn(1, 100)
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



fun evaluateRecording(context: Context): Int {
//    val audioFilePath = File(context.getExternalFilesDir(null), currentFileName).absolutePath
//
//    Log.d("Evaluation", "Evaluando archivo de audio: $currentFileName en la ruta: $audioFilePath")
//
//    val audioFile = File(audioFilePath)
//
//    if (!audioFile.exists()) {
//        Log.e("Evaluation", "El archivo de audio no existe en la ubicación esperada: $audioFilePath")
//        return 0
//    }
//
//    var maxAmplitude = 0
//    val mediaPlayer = MediaPlayer()
//    try {
//        mediaPlayer.setDataSource(audioFile.path)
//        mediaPlayer.prepare()
//
//
//        val timer = object : CountDownTimer(mediaPlayer.duration.toLong(), 100) {
//            override fun onTick(millisUntilFinished: Long) {
//                val amplitude = mediaPlayer.audioSessionId
//                if (amplitude > maxAmplitude) {
//                    maxAmplitude = amplitude
//                }
//            }
//
//            override fun onFinish() {
//                mediaPlayer.stop()
//                mediaPlayer.release()
//            }
//        }
//        timer.start()
//    } catch (e: Exception) {
//        Log.e("Evaluation", "Error al reproducir el archivo de audio: ${e.message}")
//        e.printStackTrace()
//        mediaPlayer.release()
//        return 0
//    }
//
//    val maxDb = 20 * log10(maxAmplitude.toDouble() / 32768.0)
//    return (maxDb / 120 * 100).toInt().coerceIn(1, 100)
    return 0
}