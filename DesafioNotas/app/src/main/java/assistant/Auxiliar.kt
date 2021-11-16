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
}