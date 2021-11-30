package connection

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import assistant.Auxiliar

class AdminSQLiteConnection(
    context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createNotas())
        db.execSQL(createNotasTexto())
        db.execSQL(createTareas())
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    private fun createNotas(): String {
        return "CREATE TABLE ${Auxiliar.TABLA__NOTAS}(" +
                "${Auxiliar.ID__NOTAS} int primary key," +
                "${Auxiliar.FECHA__NOTAS} text," +
                "${Auxiliar.HORA__NOTAS} text," +
                "${Auxiliar.ASUNTO__NOTAS} text," +
                "${Auxiliar.TIPO__NOTAS} int)"
    }

    private fun createNotasTexto(): String {
        return "CREATE TABLE ${Auxiliar.TABLA__NOTAS_TEXTO}(" +
                "${Auxiliar.ID__NOTAS_TEXTO} int primary key," +
                "${Auxiliar.TEXTO__NOTAS_TEXTO} text," +
                "CONSTRAINT FK_NOTA FOREIGN KEY (${Auxiliar.ID__NOTAS_TEXTO}) " +
                "REFERENCES ${Auxiliar.TABLA__NOTAS} (${Auxiliar.ID__NOTAS}) ON DELETE CASCADE)"
    }

    private fun createTareas(): String {
        return "CREATE TABLE ${Auxiliar.TABLA__TAREAS}(" +
                "${Auxiliar.ID__TAREAS} int primary key," +
                "${Auxiliar.ID_NOTA__TAREAS} int," +
                "${Auxiliar.TAREA__TAREAS} text," +
                "${Auxiliar.REALIZADA__TAREAS} bit," +
                "${Auxiliar.IMAGEN__TAREAS} blob," +
                "CONSTRAINT FK_NOTA FOREIGN KEY (${Auxiliar.ID_NOTA__TAREAS}) " +
                "REFERENCES ${Auxiliar.TABLA__NOTAS} (${Auxiliar.ID__NOTAS}) ON DELETE CASCADE)"
    }
}