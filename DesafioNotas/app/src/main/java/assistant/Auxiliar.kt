package assistant

import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory

import android.graphics.Bitmap

import android.graphics.Bitmap.CompressFormat
import java.io.ByteArrayOutputStream


object Auxiliar {
    val FORMATO_FECHA = "dd/MM/yyyy"
    val FORMATO_HORA = "hh:mm"

    fun fechaActual(): String {
        return SimpleDateFormat(FORMATO_FECHA).format(Date())
    }

    fun horaActual(): String {
        return SimpleDateFormat(FORMATO_HORA).format(Date())
    }

    //BASE DE DATOS
    val NOMBREBD = "notas.db"

    //tabla Notas
    var nextIdNota = 1
    val TABLA__NOTAS = "NOTAS"
    val ID__NOTAS = "ID"
    val FECHA__NOTAS = "FECHA"
    val HORA__NOTAS = "HORA"
    val ASUNTO__NOTAS = "ASUNTO"
    val TIPO__NOTAS = "TIPO"

    fun getNextIDnota(): Int {
        return nextIdNota++
    }

    //tabla notasTexto
    val TABLA__NOTAS_TEXTO = "NOTAS_TEXTO"
    val ID__NOTAS_TEXTO = "ID"
    val TEXTO__NOTAS_TEXTO = "TEXTO"

    //Tabla Tareas
    var nextIdTarea = 1
    val TABLA__TAREAS = "TAREAS"
    val ID__TAREAS = "IDfe"
    val ID_NOTA__TAREAS = "ID_NOTA"
    val TAREA__TAREAS = "TAREA"
    val REALIZADA__TAREAS = "REALIZADA"
    val IMAGEN__TAREAS = "IMAGEN"

    fun getNextIDTarea(): Int {
        return nextIdTarea++
    }

    fun getBytes(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    // convert from byte array to bitmap
    fun getImage(image: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }
}