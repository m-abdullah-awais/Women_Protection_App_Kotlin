package com.example.the_sos_application

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.example.the_sos_application.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Render HTML content for bold text in terms
        binding.tvTermsContent.text = Html.fromHtml(getString(R.string.terms_content), Html.FROM_HTML_MODE_COMPACT)
    }
}
