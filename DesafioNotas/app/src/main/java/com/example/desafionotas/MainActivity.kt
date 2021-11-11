package com.example.desafionotas

import adapters.NotasAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import model.Nota

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
        //notasList =
        adaptador = NotasAdapter(this, notasList)
    }
}