package com.example.the_sos_application

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.the_sos_application.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnResetPassword.setOnClickListener {
            val email = binding.tilForgotEmail.editText?.text.toString().trim()
            binding.tilForgotEmail.error = null

            if (!ValidationUtils.isValidEmail(email)) {
                binding.tilForgotEmail.error = "Email must be a valid @gmail.com address"
                return@setOnClickListener
            }

            // Disable UI
            binding.progressBar.visibility = android.view.View.VISIBLE
            binding.btnResetPassword.isEnabled = false

            FirebaseAuthHelper.getAuth().sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnResetPassword.isEnabled = true
                    Toast.makeText(this, "Reset link sent to your email.", Toast.LENGTH_LONG).show()
                    finish() // Close activity on success
                }
                .addOnFailureListener { e ->
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnResetPassword.isEnabled = true
                    Toast.makeText(this, "Failed to send reset link: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }
}
