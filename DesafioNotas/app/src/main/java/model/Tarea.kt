package model

import android.graphics.Bitmap
import java.io.Serializable

data class Tarea(var id:Int, var tarea: String, var realizada: Boolean = false, var img: Bitmap? = null):Serializable
