package com.example.the_sos_application

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.the_sos_application.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmitLogin.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString().trim()
            val password = binding.tilPassword.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                android.widget.Toast.makeText(this, "Please fill all fields", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuthHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    android.widget.Toast.makeText(this, "Login Successful", android.widget.Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                .addOnFailureListener { e ->
                    android.widget.Toast.makeText(this, "Login Failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
