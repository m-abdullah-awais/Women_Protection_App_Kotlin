package com.example.the_sos_application

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        val btnEdit = view.findViewById<Button>(R.id.btnEditProfile)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        val btnGuide = view.findViewById<Button>(R.id.btnUserGuide)

        updateUI()

        btnEdit.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        btnGuide.setOnClickListener {
            startActivity(Intent(requireContext(), GuideActivity::class.java))
        }

        btnLogout.setOnClickListener {
            MockData.resetSelection()
            val intent = Intent(requireContext(), StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        val user = MockData.currentUser
        tvName.text = user.name
        tvEmail.text = "${user.email}\n${user.phone}"
    }
}
