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


private val recorder = MediaRecorder()
private var outputFile: String = ""
private var mediaPlayer: MediaPlayer? = null

// Variables globales para almacenar el archivo de salida y el nombre del archivo.
private lateinit var currentFileName: String

// Agrega una variable global para almacenar la ruta del archivo de audio
private lateinit var audioFilePath: String

fun startRecording(context: Context) {
    // Genera un nombre de archivo único con timestamp
    val timestamp = System.currentTimeMillis()
    currentFileName = "audiorecord_$timestamp.3ga"

    // Log del nombre del archivo
    Log.d("AudioRecorderController", "Nombre del archivo de audio: ${
        context.getExternalFilesDir(
            null
        )?.absolutePath
    }/$currentFileName\"")

    outputFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val audioDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, currentFileName)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp")
            put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/")
        }
        val audioUri = resolver.insert(audioCollection, audioDetails)
        resolver.openFileDescriptor(audioUri!!, "w")?.fileDescriptor?.let {
            recorder.setOutputFile(it)
            audioUri.toString()
        } ?: throw IOException("Failed to create MediaStore entry")
    } else {
        "${context.getExternalFilesDir(null)?.absolutePath}/$currentFileName"
    }

    // Guarda la ruta del archivo de audio
    audioFilePath = outputFile

    try {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }
    } catch (e: IOException) {
        Log.e("AudioRecorderController", "prepare() failed")
        Toast.makeText(context, "Error al grabar audio", Toast.LENGTH_SHORT).show()
    }
}

fun stopRecording(context: Context) {
    try {
        recorder.stop()
        recorder.reset()
        Toast.makeText(context, "Grabación exitosa", Toast.LENGTH_SHORT).show()
    } catch (e: RuntimeException) {
        Log.e("AudioRecorderController", "stop() failed")
        Toast.makeText(context, "Error al detener la grabación", Toast.LENGTH_SHORT).show()
    }
}

fun playRecording(context: Context, onCompletion: () -> Unit) {
    mediaPlayer = MediaPlayer().apply {
        try {
            // Utiliza la ruta del archivo de audio almacenada
            setDataSource(context, Uri.parse(audioFilePath))
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


fun stopPlaying() {
    mediaPlayer?.release()
    mediaPlayer = null
}

fun evaluateRecording(context: Context): Int {
    val externalStorageDir = context.getExternalFilesDir(null)

    // Utiliza el nombre del archivo actual con la extensión .3ga para evaluar la grabación.
    val audioFile = File(externalStorageDir, currentFileName)

    if (!audioFile.exists()) {
        Log.e("Evaluation", "El archivo de audio no existe en la ubicación esperada: ${audioFile.absolutePath}")
        return 0 // No hay archivo para evaluar
    }

    var maxAmplitude = 0
    val mediaPlayer = MediaPlayer()
    try {
        mediaPlayer.setDataSource(audioFile.absolutePath)
        mediaPlayer.prepare()
        mediaPlayer.start()

        // Utiliza un temporizador para verificar la amplitud mientras se reproduce el audio
        val timer = object : CountDownTimer(mediaPlayer.duration.toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                val amplitude = mediaPlayer.audioSessionId
                if (amplitude > maxAmplitude) {
                    maxAmplitude = amplitude
                }
            }

            override fun onFinish() {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }
        timer.start()
    } catch (e: Exception) {
        Log.e("Evaluation", "Error al reproducir el archivo de audio: ${e.message}")
        e.printStackTrace()
        mediaPlayer.release()
        return 0
    }

    // Convierte la amplitud máxima en un valor de 1 a 100
    val maxDb = 20 * log10(maxAmplitude.toDouble() / 32768.0)
    return (maxDb / 120 * 100).toInt().coerceIn(1, 100)
}