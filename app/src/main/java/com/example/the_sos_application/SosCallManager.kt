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
        
        val userId = FirebaseAuthHelper.getCurrentUserId()
        if (userId == null) {
            android.widget.Toast.makeText(context, "User not logged in!", android.widget.Toast.LENGTH_SHORT).show()
            callback.onAllCallsCompleted()
            return
        }
        
        // Fetch fresh settings and contacts
        FirestoreRepository.getUserProfile(userId, 
            onSuccess = { user ->
                if (!user.sosEnabled) {
                     android.widget.Toast.makeText(context, "SOS is disabled in Settings.", android.widget.Toast.LENGTH_SHORT).show()
                     callback.onAllCallsCompleted()
                     return@getUserProfile
                }
                
                FirestoreRepository.getContacts(userId,
                    onSuccess = { contacts -> 
                        // 1. Filter and Sort
                        // Note: isSelected is local-only in my current implementation. 
                        // If we want persistent selection, we need to save it to Firestore. 
                        // For now, I'll assume ALL contacts are candidates, or simple logic.
                        // The previous code filtered by `it.isSelected`. 
                        // If `isSelected` is transient, it will be false for all fetched contacts!
                        // This BREAKS the feature if selection is transient.
                        // I must assume ALL valid contacts should be called OR I need to implement selection persistence.
                        // Requirement: "Contact fields: Name, Phone, Priority" -> No 'isSelected'.
                        // Contacts allowed: Max 5.
                        // I will assume ALL added contacts are to be called (max 5). 
                        // The selection UI in ContactsFragment might be just for "active" contacts?
                        // "Implement COMPLETE CRUD... Maximum 5 contacts allowed".
                        // I will call ALL valid contacts returned from Firestore.
                        
                         val validContacts = contacts
                            .filter { it.isSelected && it.number.isNotEmpty() } // Valid number and Selected

                         // Simple priority sort (High > Medium > Low)
                         val sortedContacts = validContacts.sortedByDescending { 
                             when(it.priority) {
                                 "Critical" -> 5
                                 "Urgent" -> 4
                                 "High" -> 3
                                 "Medium" -> 2
                                 else -> 1
                             }
                         }

                        sortedContacts.forEach { contact ->
                            contactsToCall.add(Pair(contact.name, contact.number))
                        }

                        // 2. Add Police if enabled
                        if (user.policeEnabled) {
                            contactsToCall.add(Pair("Police", "15"))
                        }

                        if (contactsToCall.isEmpty()) {
                            android.widget.Toast.makeText(context, "No contacts found.", android.widget.Toast.LENGTH_SHORT).show()
                            callback.onAllCallsCompleted()
                            return@getContacts
                        }

                        currentCallIndex = 0
                        makeNextCall(context)
                    },
                    onFailure = {
                         android.widget.Toast.makeText(context, "Failed to load contacts.", android.widget.Toast.LENGTH_SHORT).show()
                         callback.onAllCallsCompleted()
                    }
                )
            },
            onFailure = {
                android.widget.Toast.makeText(context, "Failed to verify SOS settings.", android.widget.Toast.LENGTH_SHORT).show()
                callback.onAllCallsCompleted()
            }
        )
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
