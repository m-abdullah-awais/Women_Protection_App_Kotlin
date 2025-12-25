package com.example.the_sos_application

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.example.the_sos_application.databinding.ActivityPrivacyBinding

class PrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Render HTML content for bold text in privacy policy
        binding.tvPrivacyContent.text = Html.fromHtml(getString(R.string.privacy_content), Html.FROM_HTML_MODE_COMPACT)
    }
}
