package com.example.desafionotas

import adapters.NotasAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assistant.Auxiliar
import assistant.TipoNota
import connection.Conexion
import model.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var rv: RecyclerView
    lateinit var adaptador: NotasAdapter
    lateinit var notasList: ArrayList<Nota>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.rvNotas)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        notasList = Conexion.getNotas(this)
        adaptador = NotasAdapter(this, notasList)
        rv.adapter = adaptador

        Auxiliar.nextIdNota = Conexion.getNextIdNota(this)
        Auxiliar.nextIdTarea = Conexion.getNextIdTarea(this)
    }

    fun addNota(view: View) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloTipo))
            .setMessage(getString(R.string.strMensajeTipo))
            .setPositiveButton(getString(R.string.strTipoTexto)) { view, _ ->
                pedirAsunto(TipoNota.TEXTO)
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strTipoTareas)) { view, _ ->
                pedirAsunto(TipoNota.LISTA_TAREAS)
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun pedirAsunto(tipoNota: TipoNota) {
        var asunto = ""
        val dialogView = layoutInflater.inflate(R.layout.asunto_view, null)
        val txtAsunto = dialogView.findViewById<EditText>(R.id.edAsunto)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloAsunto))
            .setView(dialogView)
            .setPositiveButton("OK") { view, _ ->
                asunto = txtAsunto.text.toString().trim()
                asunto = if (asunto.isNotEmpty()) asunto
                else "Sin asunto"
                crearNota(tipoNota, asunto)
                rv.adapter = NotasAdapter(this, Conexion.getNotas(this))
                view.dismiss()

            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun crearNota(tipoNota: TipoNota, asunto: String) {
        when (tipoNota) {
            TipoNota.TEXTO -> {
                Conexion.addNota(
                    this,
                    NotaTexto(
                        Auxiliar.getNextIDnota(),
                        Auxiliar.fechaActual(),
                        Auxiliar.horaActual(),
                        asunto
                    )
                )
            }
            TipoNota.LISTA_TAREAS -> {
                Conexion.addNota(
                    this,
                    NotaTareas(
                        Auxiliar.getNextIDnota(),
                        Auxiliar.fechaActual(),
                        Auxiliar.horaActual(),
                        asunto
                    )
                )
            }
        }
    }

    fun editar(view: View) {
        val nota = adaptador.getSelected()
        Toast.makeText(this, "Abriendo ${nota.asunto}", Toast.LENGTH_SHORT).show()
    }
}