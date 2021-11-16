package model

import assistant.TipoNota
import java.util.*

class NotaTexto(id: Int, fecha: String, hora: String, asunto: String, var texto: String = "") :
    Nota(
        id, fecha, hora,
        asunto, tipo = TipoNota.TEXTO
    ) {
}