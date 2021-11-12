package com.example.desafionotas

import adapters.NotasAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assistant.Constantes
import model.*
import java.text.SimpleDateFormat
import java.util.*
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
        notasList = ArrayList(0)
        adaptador = NotasAdapter(this, notasList)
        rv.adapter = adaptador
    }

    fun addNota(view: View) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.strTituloTipo))
            .setMessage(getString(R.string.strMensajeTipo))
            .setPositiveButton(getString(R.string.strTipoTexto)) { view, _ ->
                notasList.add(
                    NotaTexto(
                        1,
                        SimpleDateFormat(Constantes.FORMATO_FECHA).format(Date()),
                        SimpleDateFormat(Constantes.FORMATO_HORA).format(Date()),
                        "Prueba",
                        "Probando notas de texto"
                    )
                )
                rv.adapter = NotasAdapter(this, notasList)
                view.dismiss()
            }
            .setNegativeButton(getString(R.string.strTipoTareas)) { view, _ ->
                //abrir ventana de tareas
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }
    fun clickLayout(view: View){
        adaptador.deseleccionar()
        Log.e("jorge","pulsado")
    }
}