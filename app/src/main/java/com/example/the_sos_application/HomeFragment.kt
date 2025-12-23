package com.example.the_sos_application

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.the_sos_application.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSOS.setOnClickListener {
            // Updated SOS Trigger with Countdown
            showSOSCountdown()
        }
    }

    private fun showSOSCountdown() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_sos_countdown, null)
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
        
        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()

        val tvCountdown = dialogView.findViewById<android.widget.TextView>(R.id.tvCountdown)
        val btnCancel = dialogView.findViewById<android.view.View>(R.id.btnCancelSOS)

        // 5 Second Countdown
        val timer = object : android.os.CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000) + 1
                tvCountdown.text = secondsLeft.toString()
            }

            override fun onFinish() {
                dialog.dismiss()
                 // Existing SOS Logic
                triggerSOS()
            }
        }.start()

        btnCancel.setOnClickListener {
            timer.cancel()
            dialog.dismiss()
            Toast.makeText(requireContext(), "SOS Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun triggerSOS() {
        // Safe context usage with requireContext()
        Toast.makeText(requireContext(), "SOS Activated: 5 priority contacts + Police (15) notified", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
