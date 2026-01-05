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
        // Load state from Firestore
        val userId = FirebaseAuthHelper.getCurrentUserId() ?: return
        
        // binding.progressBar?.visibility = View.VISIBLE // Assuming progress bar exists or added dynamically, or just ignore UI feedback for now if missing
        
        FirestoreRepository.getUserProfile(userId,
            onSuccess = { user ->
                // binding.progressBar?.visibility = View.GONE
                binding.switchEnableSos.isChecked = user.sosEnabled
                binding.switchIncludePolice.isChecked = user.policeEnabled
                
                // Update local singleton as cache
                SosSettingsManager.isSosEnabled = user.sosEnabled
                SosSettingsManager.isPoliceEnabled = user.policeEnabled
                
                updateDependencyUI(user.sosEnabled)
            },
            onFailure = {
                // binding.progressBar?.visibility = View.GONE
                android.widget.Toast.makeText(this, "Failed to load settings", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.switchEnableSos.setOnCheckedChangeListener { _, isChecked ->
            SosSettingsManager.isSosEnabled = isChecked
            updateDependencyUI(isChecked)
            saveSettings()
        }

        binding.switchIncludePolice.setOnCheckedChangeListener { _, isChecked ->
            SosSettingsManager.isPoliceEnabled = isChecked
            saveSettings()
        }
    }

    private fun saveSettings() {
        val userId = FirebaseAuthHelper.getCurrentUserId() ?: return
        val sosEnabled = binding.switchEnableSos.isChecked
        val policeEnabled = binding.switchIncludePolice.isChecked
        
        FirestoreRepository.updateUserSettings(userId, sosEnabled, policeEnabled,
            onSuccess = {
               // Log.d("SosSettings", "Settings saved")
            },
            onFailure = {
               android.widget.Toast.makeText(this, "Failed to save settings", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
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
