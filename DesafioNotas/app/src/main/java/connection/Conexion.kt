package connection

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import assistant.Auxiliar
import assistant.TipoNota
import model.Nota
import model.NotaTareas
import model.NotaTexto
import model.Tarea

object Conexion {
    private var nombreBD = Auxiliar.NOMBREBD

    fun addNota(context: Context, nota: Nota) {
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = ContentValues()
        reg.put(Auxiliar.ID__NOTAS, nota.id)
        reg.put(Auxiliar.FECHA__NOTAS, nota.fecha)
        reg.put(Auxiliar.HORA__NOTAS, nota.hora)
        reg.put(Auxiliar.ASUNTO__NOTAS, nota.asunto)
        reg.put(Auxiliar.TIPO__NOTAS, if (nota.tipo == TipoNota.TEXTO) 0 else 1)
        bd.insert(Auxiliar.TABLA__NOTAS, null, reg)
        bd.close()
    }

    fun addTarea(context: Context, idNota: Int, tarea: Tarea) {
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = ContentValues()
        reg.put(Auxiliar.ID__TAREAS, tarea.id)
        reg.put(Auxiliar.ID_NOTA__TAREAS, idNota)
        reg.put(Auxiliar.TAREA__TAREAS, tarea.tarea)
        reg.put(Auxiliar.REALIZADA__TAREAS, tarea.realizada)
        reg.put(Auxiliar.IMAGEN__TAREAS, tarea.img)
        bd.insert(Auxiliar.TABLA__TAREAS, null, reg)
    }

    fun addTexto(context: Context, idNota: Int, texto: String) {
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = ContentValues()
        reg.put(Auxiliar.ID__NOTAS_TEXTO, idNota)
        reg.put(Auxiliar.TEXTO__NOTAS_TEXTO, texto)
        bd.insert(Auxiliar.TABLA__NOTAS_TEXTO, null, reg)
    }

    fun numNotas(context: Context): Int {
        var contador = 0
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery(
            "select ${Auxiliar.ID__NOTAS} from ${Auxiliar.TABLA__NOTAS}",
            null
        )
        while (fila.moveToNext()) {
            contador++
        }
        bd.close()
        return contador
    }

    fun getNotas(context: Context): ArrayList<Nota> {
        var notas = ArrayList<Nota>(0)
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = bd.rawQuery(
            "select ${Auxiliar.ID__NOTAS}, ${Auxiliar.FECHA__NOTAS}, ${Auxiliar.HORA__NOTAS}, ${Auxiliar.ASUNTO__NOTAS}, ${Auxiliar.TIPO__NOTAS} from ${Auxiliar.TABLA__NOTAS}",
            null
        )
        while (reg.moveToNext()) {
            val id = reg.getInt(0)
            val tipo = reg.getInt(4)
            when (tipo) {
                0 ->
                    notas.add(
                        NotaTexto(
                            id,
                            reg.getString(1),
                            reg.getString(2),
                            reg.getString(3),
                            getTexto(bd, id)
                        )
                    )
                1 ->
                    notas.add(
                        NotaTareas(
                            id,
                            reg.getString(1),
                            reg.getString(2),
                            reg.getString(3),
                            getTareas(bd, id)
                        )
                    )
            }
        }
        bd.close()
        return notas
    }

    private fun getTexto(bd: SQLiteDatabase, idNota: Int): String {
        val reg = bd.rawQuery(
            "select ${Auxiliar.TEXTO__NOTAS_TEXTO} from ${Auxiliar.TABLA__NOTAS_TEXTO} where ${Auxiliar.ID__NOTAS_TEXTO}=$idNota",
            null
        )
        return if (reg.moveToNext())
            reg.getString(0)
        else ""
    }

    private fun getTareas(bd: SQLiteDatabase, idNota: Int): ArrayList<Tarea> {
        val tareas = ArrayList<Tarea>(0)
        val reg = bd.rawQuery(
            "select ${Auxiliar.ID__TAREAS} ${Auxiliar.TAREA__TAREAS}, ${Auxiliar.REALIZADA__TAREAS}, ${Auxiliar.IMAGEN__TAREAS} from ${Auxiliar.TABLA__TAREAS} where ${Auxiliar.ID_NOTA__TAREAS}=$idNota",
            null
        )
        while (reg.moveToNext()) {
            tareas.add(
                Tarea(
                    reg.getInt(0),
                    reg.getString(1),
                    reg.getInt(2) == 1,
                    reg.getString(3)
                )
            )
        }
        return tareas
    }

    fun modAsuntoNota(context: Context, nota: Nota, nuevoAsunto: String): Int {
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = ContentValues()
        reg.put(Auxiliar.HORA__NOTAS, Auxiliar.horaActual())
        reg.put(Auxiliar.ASUNTO__NOTAS, nuevoAsunto)
        val can = bd.update(
            Auxiliar.TABLA__NOTAS,
            reg,
            "${Auxiliar.ID__NOTAS}=${nota.id}",
            null
        )
        bd.close()
        return can
    }

    fun modTextoNota(context: Context, nota: Nota, nuevoTexto: String): Int {
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        var reg = ContentValues()
        reg.put(Auxiliar.TEXTO__NOTAS_TEXTO, nuevoTexto)
        val cantidad = bd.update(
            Auxiliar.TABLA__NOTAS_TEXTO,
            reg,
            "${Auxiliar.ID__NOTAS_TEXTO}=${nota.id}",
            null
        )
        if (cantidad > 0) {
            reg = ContentValues()
            reg.put(Auxiliar.HORA__NOTAS, Auxiliar.horaActual())
            bd.update(
                Auxiliar.TABLA__NOTAS,
                reg,
                "${Auxiliar.ID__NOTAS}=${nota.id}",
                null
            )
        }
        bd.close()
        return cantidad
    }

    fun delNota(context: Context, nota: Nota): Int {
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val cantidad =
            bd.delete(
                Auxiliar.TABLA__NOTAS,
                "${Auxiliar.ID__NOTAS}=${nota.id}",
                null
            )
        bd.close()
        return cantidad
    }

    fun getNextIdNota(context: Context): Int {
        var nextID = 1
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = bd.rawQuery("select ${Auxiliar.ID__NOTAS} from ${Auxiliar.TABLA__NOTAS}", null)
        if (reg.moveToLast()) {
            nextID = reg.getInt(0) + 1
        }
        bd.close()
        return nextID
    }

    fun getNextIdTarea(context: Context): Int {
        var nextID = 1
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = bd.rawQuery("select ${Auxiliar.ID__TAREAS} from ${Auxiliar.TABLA__TAREAS}", null)
        if (reg.moveToLast()) {
            nextID = reg.getInt(0) + 1
        }
        bd.close()
        return nextID
    }
}