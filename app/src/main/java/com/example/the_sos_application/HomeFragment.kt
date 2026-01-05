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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.widget.TextView
import android.app.AlertDialog

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
        if (!SosSettingsManager.isSosEnabled) {
            Toast.makeText(requireContext(), "SOS is currently disabled in settings", Toast.LENGTH_SHORT).show()
            return
        }

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
        val timer = object : android.os.CountDownTimer(3000, 1000) {
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
        if (checkPermissions()) {
            startSosFlow()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        val resultCall = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
        val resultRead = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)
        return resultCall == PackageManager.PERMISSION_GRANTED && resultRead == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE),
            100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSosFlow()
            } else {
                Toast.makeText(requireContext(), "Permissions required for SOS calling", Toast.LENGTH_LONG).show()
            }
        }
    }

    private var callDialog: AlertDialog? = null
    private var callTimer: android.os.Handler? = null
    private var callRunnable: Runnable? = null

    private fun startSosFlow() {
        // Show Custom Call Progress Dialog
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_sos_calling, null)
        val tvContactName = dialogView.findViewById<TextView>(R.id.tvContactName)
        val tvContactNumber = dialogView.findViewById<TextView>(R.id.tvContactNumber)
        val btnCancel = dialogView.findViewById<View>(R.id.btnCancelSos)
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setCancelable(false)
        
        callDialog = builder.create()
        callDialog?.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        callDialog?.show()

        btnCancel.setOnClickListener {
            SosCallManager.cancelSos()
            stopCallTimer()
            callDialog?.dismiss()
            Toast.makeText(requireContext(), "SOS Cancelled", Toast.LENGTH_SHORT).show()
        }

        SosCallManager.startSosSequence(requireContext(), object : SosCallManager.CallCallback {
            override fun onCallStarted(contactName: String, contactNumber: String) {
                // Update Dialog UI
                tvContactName.text = contactName
                tvContactNumber.text = contactNumber
                
                // Start 20s timer for next call (only if not cut)
                startCallTimer()
                
                // Enable return detection after a short delay to allow app to pause
                Handler(Looper.getMainLooper()).postDelayed({
                    waitingForCallReturn = true
                }, 2000) // 2 second buffer
            }

            override fun onAllCallsCompleted() {
                stopCallTimer()
                callDialog?.dismiss()
                Toast.makeText(requireContext(), "All emergency calls completed", Toast.LENGTH_LONG).show()
            }
        })
    }



    private fun startCallTimer() {
        stopCallTimer()
        callTimer = android.os.Handler(android.os.Looper.getMainLooper())
        callRunnable = Runnable {
            // Timer finished, try next call
            Toast.makeText(requireContext(), "No response, calling next...", Toast.LENGTH_SHORT).show()
            SosCallManager.makeNextCall(requireContext())
        }
        callTimer?.postDelayed(callRunnable!!, 20000) // 20 seconds
    }

    private fun stopCallTimer() {
        callRunnable?.let { callTimer?.removeCallbacks(it) }
    }

    private var waitingForCallReturn = false

    override fun onResume() {
        super.onResume()
        if (waitingForCallReturn) {
            waitingForCallReturn = false
            // User returned to app, likely call ended or cut
            Toast.makeText(requireContext(), "Proceeding to next contact...", Toast.LENGTH_SHORT).show()
            
            stopCallTimer()
            SosCallManager.makeNextCall(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopCallTimer()
    }
}
