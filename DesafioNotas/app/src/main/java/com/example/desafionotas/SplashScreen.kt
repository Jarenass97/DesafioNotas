package com.example.desafionotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var intentVS = Intent(this,MainActivity::class.java)
        startActivity(intentVS)
        finish()
    }
}