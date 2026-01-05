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

        val userId = FirebaseAuthHelper.getCurrentUserId()
        if (userId == null) {
            finish()
            return
        }

        // Load existing data
        FirestoreRepository.getUserProfile(userId,
            onSuccess = { user ->
                binding.etEditName.setText(user.name)
                binding.etEditEmail.setText(user.email)
                // Phone is not stored/retrieved
                binding.etEditBloodType.setText(user.bloodGroup)
                binding.etEditAge.setText(user.age)
                binding.etEditMedical.setText(user.medicalHistory)
                binding.etEditNotes.setText(user.emergencyNotes)
            },
            onFailure = {
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        )

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString().trim()
            val newEmail = binding.etEditEmail.text.toString().trim()
            val newBlood = binding.etEditBloodType.text.toString().trim()
            val newAge = binding.etEditAge.text.toString().trim()
            val newMedical = binding.etEditMedical.text.toString().trim()
            val newNotes = binding.etEditNotes.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Name and Email required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create updated profile object
            // We need to preserve current sosEnabled/policeEnabled settings if possible, 
            // or we assume getUserProfile was called and we can just re-save.
            // A safer way is to fetch specific fields or just update these fields?
            // Firestore set(obj) overwrites unless Merge is used. 
            // Our repository saveUserProfile uses set(obj). 
            // So we should ideally have the full object. 
            // Since we loaded it, we can update the loaded object. 
            // But here I'm creating a NEW object. This risks overwriting settings to default.
            // I should modify `saveUserProfile` to use `SetOptions.merge()` OR fetch the user again.
            // Better: update `saveUserProfile` or just use map update.
            // Let's rely on FirestoreRepository to handle it or just do a merge manually here?
            // I'll update FirestoreRepository to implement `updateUserProfile` with map for partial updates, 
            // OR I will read the full profile object inside the callback.
            
            // Simpler approach for now: Just update the fields we edited.
             val updates = mapOf(
                "name" to newName,
                "email" to newEmail,
                "bloodGroup" to newBlood,
                "age" to newAge,
                "medicalHistory" to newMedical,
                "emergencyNotes" to newNotes
            )
            
            // I need a method in Repository for generic updates or specific profile update.
            // I'll stick to `saveUserProfile` but be careful.
            // Actually, I should update `saveUserProfile` to merge.
            
            FirestoreRepository.getUserProfile(userId, { currentUser ->
                 val updatedUser = currentUser.copy(
                    name = newName,
                    email = newEmail,
                    bloodGroup = newBlood,
                    age = newAge,
                    medicalHistory = newMedical,
                    emergencyNotes = newNotes
                 )
                 
                 FirestoreRepository.saveUserProfile(userId, updatedUser,
                    onSuccess = {
                        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onFailure = { e ->
                        Toast.makeText(this, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                 )
            }, {
                Toast.makeText(this, "Error fetching original profile", Toast.LENGTH_SHORT).show()
            })
        }
    }
}
