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
        if (nota.tipo == TipoNota.TEXTO) addTexto(bd, nota.id)
        bd.close()
    }

    private fun addTexto(bd: SQLiteDatabase, idNota: Int) {
        val reg = ContentValues()
        reg.put(Auxiliar.ID__NOTAS_TEXTO, idNota)
        reg.put(Auxiliar.TEXTO__NOTAS_TEXTO, "")
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
                            reg.getString(3)
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

    fun getTareas(context: Context, idNota: Int): ArrayList<Tarea> {
        val tareas = ArrayList<Tarea>(0)
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        val reg = bd.rawQuery(
            "select ${Auxiliar.ID__TAREAS}, ${Auxiliar.TAREA__TAREAS}, ${Auxiliar.REALIZADA__TAREAS}, ${Auxiliar.IMAGEN__TAREAS} from ${Auxiliar.TABLA__TAREAS} where ${Auxiliar.ID_NOTA__TAREAS}=$idNota",
            null
        )
        while (reg.moveToNext()) {
            val imgBytes = reg.getBlob(3)
            val tarea = Tarea(
                reg.getInt(0),
                reg.getString(1),
                reg.getInt(2) == 1
            )
            if (imgBytes != null) tarea.img = Auxiliar.getImage(imgBytes)
            tareas.add(tarea)
        }
        bd.close()
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


    fun guardarTareas(
        context: Context,
        nota: Nota,
        listaTareas: ArrayList<Tarea>,
        eliminables: ArrayList<Tarea>
    ) {
        val admin = AdminSQLiteConnection(context, nombreBD, null, 1)
        val bd = admin.writableDatabase
        for (t in listaTareas) {
            val reg = bd.rawQuery(
                "select * from ${Auxiliar.TABLA__TAREAS} where ${Auxiliar.ID__TAREAS} like ${t.id}",
                null
            )
            val tarea = ContentValues()
            if (reg.moveToNext()) {
                tarea.put(Auxiliar.TAREA__TAREAS, t.tarea)
                tarea.put(Auxiliar.REALIZADA__TAREAS, if (t.realizada) 1 else 0)
                if (t.img != null) tarea.put(Auxiliar.IMAGEN__TAREAS, Auxiliar.getBytes(t.img!!))
                bd.update(Auxiliar.TABLA__TAREAS, tarea, "${Auxiliar.ID__TAREAS}=${t.id}", null)
            } else {
                tarea.put(Auxiliar.ID__TAREAS, t.id)
                tarea.put(Auxiliar.ID_NOTA__TAREAS, nota.id)
                tarea.put(Auxiliar.TAREA__TAREAS, t.tarea)
                tarea.put(Auxiliar.REALIZADA__TAREAS, if (t.realizada) 1 else 0)
                if (t.img != null) tarea.put(Auxiliar.IMAGEN__TAREAS, Auxiliar.getBytes(t.img!!))
                bd.insert(Auxiliar.TABLA__TAREAS, null, tarea)
            }
        }
        for (t in eliminables) {
            bd.delete(
                Auxiliar.TABLA__TAREAS,
                "${Auxiliar.ID__TAREAS}=${t.id}",
                null
            )
        }
        bd.close()
    }
}