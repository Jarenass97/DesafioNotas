package com.example.desafionotas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewAnimator
import assistant.Auxiliar
import assistant.TipoNota
import model.Nota
import model.NotaTareas
import model.NotaTexto

class DetalleNotasActivity : AppCompatActivity() {
    lateinit var nota: Nota
    lateinit var edAsunto: EditText
    lateinit var txtLastMod: TextView
    lateinit var txtFechaHora: TextView
    lateinit var animator: ViewAnimator
    lateinit var edTexto: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_notas)
        supportActionBar?.hide()

        val bun: Bundle? = intent.extras
        nota = bun?.getSerializable("nota") as Nota
        edAsunto = findViewById(R.id.edAsuntoDetalle)
        txtLastMod = findViewById(R.id.txtUltimaEdicionDetalle)
        txtFechaHora = findViewById(R.id.txtFechaHoraDetalle)
        animator = findViewById(R.id.vaTiposNota)
        cargarDatos()
    }

    fun guardar(view: View) {
        Toast.makeText(this, "Guardando", Toast.LENGTH_SHORT).show()
    }

    fun compartir(view: View) {
        Toast.makeText(this, "Compartiendo", Toast.LENGTH_SHORT).show()
    }

    private fun cargarDatos() {
        edAsunto.append(nota.asunto)
        txtLastMod.text = getUltimaModificacion()
        txtFechaHora.text = "${nota.fecha} - ${nota.hora}"
        when (nota.tipo) {
            TipoNota.TEXTO -> {
                edTexto = findViewById(R.id.edTextoDetalle)
                edTexto.append((nota as NotaTexto).texto)
            }
            TipoNota.LISTA_TAREAS -> {
                animator.showNext()
            }
        }
    }

    private fun getUltimaModificacion(): String {
        var last = ""
        val horaMod = nota.hora
        val fechaMod = nota.fecha
        val horaActual = Auxiliar.horaActual()
        val fechaActual = Auxiliar.fechaActual()
        val hora_minutoMod = horaMod.split(":")
        val dia_mes_yearMod = fechaMod.split("/")
        val hora_minutoAct = horaActual.split(":")
        val dia_mes_yearAct = fechaActual.split("/")
        val difYears = difyears(dia_mes_yearMod[2], dia_mes_yearAct[2])
        if (difYears > 0) {
            last = "$difYears ${getString(R.string.strHaceYears)}"
        } else {
            val difMonths = difMonth(dia_mes_yearMod[1], dia_mes_yearAct[1])
            if (difMonths > 0) {
                last = "$difMonths ${getString(R.string.strHaceMeses)}"
            } else {
                val difDays = difDay(dia_mes_yearMod[0], dia_mes_yearAct[0])
                if (difDays > 0) {
                    last = "$difDays ${getString(R.string.strHaceDias)}"
                } else {
                    val difHours = difHour(hora_minutoMod[0], hora_minutoAct[0])
                    if (difHours > 0) {
                        last = "$difHours ${getString(R.string.strHaceHoras)}"
                    } else {
                        val difMinutes = difMin(hora_minutoMod[1], hora_minutoAct[1])
                        if (difMinutes > 0)
                            last = "$difMinutes ${getString(R.string.strHaceMinutos)}"
                        else last = getString(R.string.strMenosUnMin)
                    }
                }
            }
        }
        return last
    }

    private fun difMin(minMod: String, minAct: String): Int {
        return minAct.toInt() - minMod.toInt()
    }

    private fun difHour(hourMod: String, hourAct: String): Int {
        return hourAct.toInt() - hourMod.toInt()
    }

    private fun difDay(dayMod: String, dayAct: String): Int {
        return dayAct.toInt() - dayMod.toInt()
    }

    private fun difMonth(monthMod: String, monthAct: String): Int {
        return monthAct.toInt() - monthMod.toInt()
    }

    private fun difyears(yearMod: String, yearAct: String): Int {
        return yearAct.toInt() - yearMod.toInt()
    }
}