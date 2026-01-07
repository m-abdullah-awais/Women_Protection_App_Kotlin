package com.example.the_sos_application

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.the_sos_application.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmitRegister.isEnabled = false

        binding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSubmitRegister.isEnabled = isChecked
        }

        setupTermsAndPrivacyLinks()

        binding.btnSubmitRegister.setOnClickListener {
            val name = binding.tilName.editText?.text.toString().trim()
            val email = binding.tilRegEmail.editText?.text.toString().trim()
            val password = binding.tilRegPassword.editText?.text.toString().trim()
            val confirmPassword = binding.tilConfirmPassword.editText?.text.toString().trim()

            // Reset errors
            binding.tilName.error = null
            binding.tilRegEmail.error = null
            binding.tilRegPassword.error = null
            binding.tilConfirmPassword.error = null

            var isValid = true

            if (!ValidationUtils.isValidUsername(name)) {
                binding.tilName.error = "Username must contain only alphabets (A-Z, a-z)"
                isValid = false
            }

            if (!ValidationUtils.isValidEmail(email)) {
                binding.tilRegEmail.error = "Email must be a valid @gmail.com address"
                isValid = false
            }

            if (!ValidationUtils.isValidPassword(password)) {
                binding.tilRegPassword.error = "Password must be 8+ chars, with 1 Upper, 1 Lower, 1 Number"
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                binding.tilConfirmPassword.error = "Please confirm your password"
                isValid = false
            }

            if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Passwords do not match"
                isValid = false
            }

            if (!binding.cbTerms.isChecked) {
                android.widget.Toast.makeText(this, "Please agree to Terms and Privacy Policy", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValid) {
                return@setOnClickListener
            }

            // Register with Firebase
            binding.progressBar.visibility = android.view.View.VISIBLE
            binding.btnSubmitRegister.isEnabled = false

            FirebaseAuthHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid
                    if (userId != null) {
                        // Create User Profile in Firestore
                        val newUser = FirestoreRepository.UserProfile(
                            name = name,
                            email = email
                        )
                        FirestoreRepository.saveUserProfile(userId, newUser,
                            onSuccess = {
                                binding.progressBar.visibility = android.view.View.GONE
                                binding.btnSubmitRegister.isEnabled = true
                                android.widget.Toast.makeText(this, "Registration Successful", android.widget.Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finishAffinity()
                            },
                            onFailure = { e ->
                                binding.progressBar.visibility = android.view.View.GONE
                                binding.btnSubmitRegister.isEnabled = true
                                android.widget.Toast.makeText(this, "Failed to save profile: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                         binding.progressBar.visibility = android.view.View.GONE
                         binding.btnSubmitRegister.isEnabled = true
                    }
                }
                .addOnFailureListener { e ->
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnSubmitRegister.isEnabled = true
                    android.widget.Toast.makeText(this, "Registration Failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupTermsAndPrivacyLinks() {
        val fullText = getString(R.string.terms_agreement)
        val spannableString = android.text.SpannableString(fullText)
        val termsText = getString(R.string.terms_conditions)
        val privacyText = getString(R.string.privacy_policy)

        val termsStartIndex = fullText.indexOf(termsText)
        val termsEndIndex = termsStartIndex + termsText.length

        val privacyStartIndex = fullText.indexOf(privacyText)
        val privacyEndIndex = privacyStartIndex + privacyText.length

        val termsClickableSpan = object : android.text.style.ClickableSpan() {
            override fun onClick(widget: android.view.View) {
                startActivity(Intent(this@RegisterActivity, TermsActivity::class.java))
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = resources.getColor(R.color.colorPink, theme)
            }
        }

        val privacyClickableSpan = object : android.text.style.ClickableSpan() {
            override fun onClick(widget: android.view.View) {
                startActivity(Intent(this@RegisterActivity, PrivacyActivity::class.java))
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = resources.getColor(R.color.colorPink, theme)
            }
        }

        if (termsStartIndex >= 0) {
            spannableString.setSpan(termsClickableSpan, termsStartIndex, termsEndIndex, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        if (privacyStartIndex >= 0) {
            spannableString.setSpan(privacyClickableSpan, privacyStartIndex, privacyEndIndex, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.cbTerms.text = spannableString
        binding.cbTerms.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }
}
