package com.example.desafionotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        initAnimations()
        initApp()
    }

    private fun initApp() {
        val intent = Intent(this, MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            run {
                startActivity(intent)
                finish()
            }
        }, 2000)

    }

    private fun initAnimations() {
        val animacionImagen = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo)
        val animacionTitulo = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba)
        val logo = findViewById<ImageView>(R.id.imgIconoApp)
        val nombreApp = findViewById<TextView>(R.id.txtTituloApp)
        logo.startAnimation(animacionImagen)
        nombreApp.startAnimation(animacionTitulo)
    }
}