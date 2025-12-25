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
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
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
