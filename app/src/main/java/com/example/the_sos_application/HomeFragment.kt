package com.example.the_sos_application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSOS = view.findViewById<View>(R.id.btnSOS)
        btnSOS.setOnClickListener {
            // Updated to use MockData for summary
            val summary = MockData.getSOSSummary()
            Toast.makeText(requireContext(), "SOS Activated!\n$summary", Toast.LENGTH_LONG).show()
        }
    }
}
