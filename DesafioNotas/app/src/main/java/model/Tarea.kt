package model

import java.io.Serializable

data class Tarea(var id:Int, var tarea: String, var realizada: Boolean = false, var img: String? = null):Serializable
