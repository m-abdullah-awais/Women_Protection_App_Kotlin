package com.example.the_sos_application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val etName = findViewById<EditText>(R.id.etEditName)
        val etEmail = findViewById<EditText>(R.id.etEditEmail)
        val etPhone = findViewById<EditText>(R.id.etEditPhone)
        val etBlood = findViewById<EditText>(R.id.etEditBloodType)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)

        // Load existing data
        val user = MockData.currentUser
        etName.setText(user.name)
        etEmail.setText(user.email)
        etPhone.setText(user.phone)
        etBlood.setText(user.bloodType)

        btnSave.setOnClickListener {
            val newName = etName.text.toString()
            val newEmail = etEmail.text.toString()
            val newPhone = etPhone.text.toString()
            val newBlood = etBlood.text.toString()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Name and Email required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update MockData
            MockData.currentUser = MockData.UserProfile(newName, newEmail, newPhone, newBlood)
            
            Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
            finish() // Go back to ProfileFragment (onResume will refresh it)
        }
    }
}
