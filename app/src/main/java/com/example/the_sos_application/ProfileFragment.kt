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
            binding.btnLogout.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            
            FirebaseAuthHelper.logout()
            
            val intent = Intent(requireActivity(), StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadProfileData() // Refresh data when coming back from EditProfile
    }

    private fun loadProfileData() {
        val userId = FirebaseAuthHelper.getCurrentUserId()
        if (userId == null) {
             // If user is null, simply stop or redirect (though redirect is handled elsewhere)
            return
        }

        setLoadingState(true)

        FirestoreRepository.getUserProfile(userId,
            onSuccess = { user ->
                if (_binding != null) {
                    setLoadingState(false)
                    binding.tvName.text = user.name
                    binding.tvEmail.text = user.email
                    binding.tvBlood.text = user.bloodGroup
                    binding.tvAge.text = user.age
                    binding.tvMedical.text = user.medicalHistory
                    binding.tvNotes.text = user.emergencyNotes
                }
            },
            onFailure = { e ->
                if (_binding != null) {
                    setLoadingState(false)
                    if (context != null) {
                        android.widget.Toast.makeText(context, "Error loading profile: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (_binding == null) return
        
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        val contentVisibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.ivProfileImage.visibility = contentVisibility
        binding.tvName.visibility = contentVisibility
        binding.tvEmail.visibility = contentVisibility
        binding.btnEditProfile.visibility = contentVisibility
        binding.cvEmergencyInfo.visibility = contentVisibility
        binding.btnSosSettings.visibility = contentVisibility
        binding.btnLogout.visibility = contentVisibility
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
