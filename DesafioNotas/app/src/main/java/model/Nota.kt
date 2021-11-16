package model

import assistant.TipoNota
import java.util.*

open abstract class Nota(
    var id: Int,
    var fecha: String,
    var hora: String,
    var asunto: String,
    var tipo: TipoNota
) {
}