package com.example.the_sos_application

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import android.widget.TextView

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val btnRegister = findViewById<AppCompatButton>(R.id.btnStartRegister)
        val btnLogin = findViewById<AppCompatButton>(R.id.btnStartLogin)
        val btnGuest = findViewById<TextView>(R.id.btnStartGuest)



        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        
        btnGuest.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
