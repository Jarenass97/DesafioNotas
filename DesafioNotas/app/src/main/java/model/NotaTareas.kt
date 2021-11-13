package model

class NotaTareas(
    id: Int,
    fecha: String,
    hora: String,
    asunto: String,
    var tareas: ArrayList<Tarea> = ArrayList(0)
) :
    Nota(id, fecha, hora, asunto) {
    fun addTarea(tarea: String) {
        tareas.add(Tarea(tarea))
    }
}