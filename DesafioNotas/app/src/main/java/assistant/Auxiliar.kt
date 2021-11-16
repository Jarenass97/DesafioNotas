package assistant

import java.text.SimpleDateFormat
import java.util.*

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
    var nextId = 1
    val TABLA__NOTAS = "NOTAS"
    val ID__NOTAS = "ID"
    val FECHA__NOTAS = "FECHA"
    val HORA__NOTAS = "HORA"
    val ASUNTO__NOTAS = "ASUNTO"
    val TIPO__NOTAS = "TIPO"

    fun getNextID(): Int {
        return nextId++
    }

    //tabla notasTexto
    val TABLA__NOTAS_TEXTO = "NOTAS_TEXTO"
    val ID__NOTAS_TEXTO = "ID"
    val TEXTO__NOTAS_TEXTO = "TEXTO"

    //Tabla Tareas
    val TABLA__TAREAS = "TAREAS"
    val ID_NOTA__TAREAS = "ID_NOTA"
    val TAREA__TAREAS = "TAREA"
    val REALIZADA__TAREAS = "REALIZADA"
    val IMAGEN__TAREAS = "IMAGEN"
}