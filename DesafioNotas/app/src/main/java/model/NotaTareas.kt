package model

import assistant.TipoNota

class NotaTareas(
    id: Int,
    fecha: String,
    hora: String,
    asunto: String,
    var tareas: ArrayList<Tarea> = ArrayList(0)
) :
    Nota(id, fecha, hora, asunto, tipo = TipoNota.LISTA_TAREAS) {
    fun addTarea(tarea: String) {
        tareas.add(Tarea(tarea))
    }
}