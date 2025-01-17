package com.example.no_thief

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Continue_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_continue_page)
        val signUp : Button = findViewById(R.id.buttonSignUp)
        val login : Button = findViewById(R.id.buttonLogin)

        login.setOnClickListener{
            val i = Intent(this,Login::class.java)
            startActivity(i)
        }

        signUp.setOnClickListener{
            val i = Intent(this,Registration::class.java)
            startActivity(i)
        }

    }
}