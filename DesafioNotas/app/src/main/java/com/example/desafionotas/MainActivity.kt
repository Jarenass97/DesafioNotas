package com.example.desafionotas

import adapters.NotasAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assistant.Auxiliar
import assistant.TipoNota
import com.google.android.material.floatingactionbutton.FloatingActionButton
import connection.Conexion
import model.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var rv: RecyclerView
    lateinit var adaptador: NotasAdapter
    lateinit var notasList: ArrayList<Nota>
    lateinit var btnEditar: FloatingActionButton
    lateinit var edBusqueda: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_DesafioNotas)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        btnEditar = findViewById(R.id.btnEditarNota)
        rv = findViewById(R.id.rvNotas)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        notasList = Conexion.getNotas(this)
        adaptador = NotasAdapter(this, notasList)
        rv.adapter = adaptador

        edBusqueda = findViewById(R.id.edBusqueda)
        edBusqueda.addTextChangedListener(afterTextChanged = {
            val notas = Conexion.findNotasByAsunto(this, edBusqueda.text.toString())
            adaptador=NotasAdapter(this,notas)
            rv.adapter=adaptador
        })

        Auxiliar.nextIdNota = Conexion.getNextIdNota(this)
        Auxiliar.nextIdTarea = Conexion.getNextIdTarea(this)
    }

    override fun onResume() {
        super.onResume()
        notasList = Conexion.getNotas(this)
        adaptador = NotasAdapter(this, notasList)
        adaptador.deseleccionar()
        btnEditar.isVisible = false
        rv.adapter = adaptador
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
                else getString(R.string.strSinAsunto)
                crearNota(tipoNota, asunto)
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun crearNota(tipoNota: TipoNota, asunto: String) {
        lateinit var nota: Nota
        when (tipoNota) {
            TipoNota.TEXTO -> {
                nota = NotaTexto(
                    Auxiliar.getNextIDnota(),
                    Auxiliar.fechaActual(),
                    Auxiliar.horaActual(),
                    asunto
                )
            }
            TipoNota.LISTA_TAREAS -> {
                nota = NotaTareas(
                    Auxiliar.getNextIDnota(),
                    Auxiliar.fechaActual(),
                    Auxiliar.horaActual(),
                    asunto
                )
            }
        }
        Conexion.addNota(this, nota)
        abrirDetalle(nota)
        adaptador = NotasAdapter(this, Conexion.getNotas(this))
        rv.adapter = adaptador
    }

    fun editar(view: View) {
        val nota = adaptador.getSelected()
        abrirDetalle(nota)
    }

    fun abrirDetalle(nota: Nota) {
        val intent = Intent(this, DetalleNotasActivity::class.java)
        intent.putExtra("nota", nota)
        startActivity(intent)
    }
}