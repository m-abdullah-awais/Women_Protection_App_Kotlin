package com.example.the_sos_application

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.the_sos_application.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadProfileData()

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
        }

        binding.btnSosSettings.setOnClickListener {
            startActivity(Intent(requireActivity(), SosSettingsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(requireActivity(), StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadProfileData() // Refresh data when coming back from EditProfile
    }

    private fun loadProfileData() {
        val userId = FirebaseAuthHelper.getCurrentUserId()
        if (userId == null) {
            // Should prompt login or handle error
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.ivProfileImage.visibility = View.INVISIBLE
        binding.tvName.visibility = View.INVISIBLE
        binding.tvEmail.visibility = View.INVISIBLE
        binding.btnEditProfile.visibility = View.INVISIBLE
        binding.cvEmergencyInfo.visibility = View.INVISIBLE

        FirestoreRepository.getUserProfile(userId,
            onSuccess = { user ->
                binding.progressBar.visibility = View.GONE
                binding.ivProfileImage.visibility = View.VISIBLE
                binding.tvName.visibility = View.VISIBLE
                binding.tvEmail.visibility = View.VISIBLE
                binding.btnEditProfile.visibility = View.VISIBLE
                binding.cvEmergencyInfo.visibility = View.VISIBLE

                binding.tvName.text = user.name
                binding.tvEmail.text = user.email
                // Phone number removed as per requirements (not stored in user profile)
                binding.tvBlood.text = user.bloodGroup
                binding.tvAge.text = user.age
                binding.tvMedical.text = user.medicalHistory
                binding.tvNotes.text = user.emergencyNotes
            },
            onFailure = { e ->
                binding.progressBar.visibility = View.GONE
                if (context != null) {
                    android.widget.Toast.makeText(context, "Error loading profile: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
