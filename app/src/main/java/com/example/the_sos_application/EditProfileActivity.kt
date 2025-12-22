package com.example.the_sos_application

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.the_sos_application.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load existing data
        val user = MockData.currentUser
        binding.etEditName.setText(user.name)
        binding.etEditEmail.setText(user.email)
        binding.etEditPhone.setText(user.phone)
        binding.etEditBloodType.setText(user.bloodType)
        binding.etEditAge.setText(user.age)
        binding.etEditMedical.setText(user.medicalConditions)
        binding.etEditNotes.setText(user.emergencyNotes)

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString()
            val newEmail = binding.etEditEmail.text.toString()
            val newPhone = binding.etEditPhone.text.toString()
            val newBlood = binding.etEditBloodType.text.toString()
            val newAge = binding.etEditAge.text.toString()
            val newMedical = binding.etEditMedical.text.toString()
            val newNotes = binding.etEditNotes.text.toString()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Name and Email required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update MockData
            MockData.currentUser = MockData.UserProfile(
                name = newName,
                email = newEmail,
                phone = newPhone,
                bloodType = newBlood,
                age = newAge,
                medicalConditions = newMedical,
                emergencyNotes = newNotes
            )
            
            Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
