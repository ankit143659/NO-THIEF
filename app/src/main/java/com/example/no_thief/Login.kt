package com.example.no_thief

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val signUp =findViewById<TextView?>(R.id.textViewsignupp).setOnClickListener{
            val i = Intent(this,Registration::class.java)
            startActivity(i)
        }
        val login = findViewById<Button>(R.id.buttonLogin).setOnClickListener{
            val i = Intent(this,HOMEPAGE::class.java)
            startActivity(i)
            finish()
        }
    }
}