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


//private val recorder = MediaRecorder()
//var outputFile: String = ""
//
//fun startRecording(context: Context) {
//    // Guardo el fichero en la caché porque únicamente quiero sacar información de éste.
//    outputFile = "${context.externalCacheDir?.absolutePath}/audiorecordtest.3gp"
//
//    // Grabo el audio
//    try {
//        recorder.apply {
//            setAudioSource(MediaRecorder.AudioSource.MIC)
//            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//            setOutputFile(outputFile)
//            prepare()
//            start()
//        }
//    } catch (e: IOException) {
//        Log.e("AudioRecorderController", "prepare() failed")
//        Toast.makeText(context, "Error al grabar audio", Toast.LENGTH_SHORT).show()
//    }
//}
//
//// Corto el audio y controlo que se haya guardado
//
//fun stopRecording(context: Context) {
//    try {
//        recorder.stop()
//        recorder.reset()
//        Toast.makeText(context, "Grabación exitosa", Toast.LENGTH_SHORT).show()
//    } catch (e: RuntimeException) {
//        Log.e("AudioRecorderController", "stop() failed")
//        Toast.makeText(context, "Error al detener la grabación", Toast.LENGTH_SHORT).show()
//    }
//}



private val recorder = MediaRecorder()
private var outputFile: String = ""
private var mediaPlayer: MediaPlayer? = null

fun startRecording(context: Context) {
    outputFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val audioCollection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val audioDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "audiorecordtest.3gp")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp")
            put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/")
        }
        val audioUri = resolver.insert(audioCollection, audioDetails)
        resolver.openFileDescriptor(audioUri!!, "w")?.fileDescriptor?.let {
            recorder.setOutputFile(it)
            audioUri.toString()
        } ?: throw IOException("Failed to create MediaStore entry")
    } else {
        "${context.getExternalFilesDir(null)?.absolutePath}/audiorecordtest.3gp"
    }

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
            setDataSource(context, Uri.parse(outputFile))
            prepare()
            start()
            setOnCompletionListener {
                onCompletion()
            }
        } catch (e: IOException) {
            Log.e("AudioRecorderController", "play() failed")
            Toast.makeText(context, "Error al reproducir palmada", Toast.LENGTH_SHORT).show()
        }
    }
}

fun stopPlaying() {
    mediaPlayer?.release()
    mediaPlayer = null
}


// Evaluar el audio
fun evaluateRecording(context: Context): Int {
    val audioFile = File(context.getExternalFilesDir(null), "audiorecordtest.3gp")
    if (!audioFile.exists()) {
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
        e.printStackTrace()
        mediaPlayer.release()
    }

    // Convierte la amplitud máxima en un valor de 1 a 100
    val maxDb = 20 * log10(maxAmplitude.toDouble() / 32768.0)
    return (maxDb / 120 * 100).toInt().coerceIn(1, 100)
}

