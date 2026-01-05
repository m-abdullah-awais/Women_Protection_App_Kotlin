package com.example.the_sos_application

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.the_sos_application.databinding.ActivitySosSettingsBinding

class SosSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySosSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInitialState()
        setupListeners()
    }

    private fun setupInitialState() {
        // Load state from Singleton
        binding.switchEnableSos.isChecked = SosSettingsManager.isSosEnabled
        binding.switchIncludePolice.isChecked = SosSettingsManager.isPoliceEnabled

        updateDependencyUI(SosSettingsManager.isSosEnabled)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.switchEnableSos.setOnCheckedChangeListener { _, isChecked ->
            SosSettingsManager.isSosEnabled = isChecked
            updateDependencyUI(isChecked)
        }

        binding.switchIncludePolice.setOnCheckedChangeListener { _, isChecked ->
            SosSettingsManager.isPoliceEnabled = isChecked
        }
    }

    private fun updateDependencyUI(isSosEnabled: Boolean) {
        binding.switchIncludePolice.isEnabled = isSosEnabled
        
        if (isSosEnabled) {
            binding.layoutIncludePolice.alpha = 1.0f
            binding.tvPoliceDisabledWarning.visibility = View.GONE
        } else {
            binding.layoutIncludePolice.alpha = 0.5f
            binding.tvPoliceDisabledWarning.visibility = View.VISIBLE
        }
    }
}
