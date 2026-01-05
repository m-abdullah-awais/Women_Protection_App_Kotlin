package com.example.the_sos_application

import android.content.Context
import android.content.Intent
import android.net.Uri

object SosCallManager {

    interface CallCallback {
        fun onCallStarted(contactName: String, contactNumber: String)
        fun onAllCallsCompleted()
    }

    private var contactsToCall: MutableList<Pair<String, String>> = mutableListOf()
    private var currentCallIndex = 0
    private var callback: CallCallback? = null

    fun startSosSequence(context: Context, callback: CallCallback) {
        this.callback = callback
        contactsToCall.clear()
        
        // 1. Fetch, Filter, and Sort Contacts from MockData
        val sortedContacts = MockData.emergencyContactsList
            .filter { it.isSelected && it.number.isNotEmpty() } // Enabled and valid
            .sortedBy { MockData.getPriorityValue(it.priority) } // Sort by Priority

        // Add to call list
        sortedContacts.forEach { contact ->
            contactsToCall.add(Pair(contact.name, contact.number))
        }

        // 2. Add Police if enabled (Last priority)
        if (SosSettingsManager.isPoliceEnabled) {
            contactsToCall.add(Pair("Police", "15"))
        }

        if (contactsToCall.isEmpty()) {
            callback.onAllCallsCompleted() // Or show error toast
            return
        }

        currentCallIndex = 0
        makeNextCall(context)
    }

    fun makeNextCall(context: Context) {
        if (currentCallIndex < contactsToCall.size) {
            val (name, number) = contactsToCall[currentCallIndex]
            callback?.onCallStarted(name, number)
            
            initiateCall(context, number)
            
            currentCallIndex++
        } else {
            callback?.onAllCallsCompleted()
        }
    }

    private fun initiateCall(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Should be handled by permissions check in UI
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelSos() {
        contactsToCall.clear()
        currentCallIndex = 0
        callback = null
    }
}
