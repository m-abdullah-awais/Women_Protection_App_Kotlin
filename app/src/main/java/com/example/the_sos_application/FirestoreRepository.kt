package com.example.the_sos_application

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Repository for Firestore operations.
 * Handles User Profile and Contacts.
 */
object FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private const val TAG = "FirestoreRepository"

    // Collection Names
    private const val USERS_COLLECTION = "users"
    private const val CONTACTS_COLLECTION = "contacts"

    // Data Classes for Firestore
    data class UserProfile(
        var name: String = "",
        var email: String = "",
        // Phone removed
        var age: String = "",
        var bloodGroup: String = "",
        var medicalHistory: String = "",
        var emergencyNotes: String = "",
        var sosEnabled: Boolean = true,
        var policeEnabled: Boolean = false
    )

    data class Contact(
        var id: String = "", // Document ID
        var name: String = "",
        var number: String = "",
        var priority: String = "Low",
        var relationship: String = "",
        var notes: String = "",
        
        // @Exclude removed to make it persistent
        var isSelected: Boolean = false
    )

    // --- User Profile Operations ---

    /**
     * Save or Update User Profile.
     */
    fun saveUserProfile(userId: String, userProfile: UserProfile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(USERS_COLLECTION).document(userId)
            .set(userProfile)
            .addOnSuccessListener {
                Log.d(TAG, "User profile saved for $userId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error saving user profile", e)
                onFailure(e)
            }
    }

    /**
     * Get User Profile.
     */
    fun getUserProfile(userId: String, onSuccess: (UserProfile) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(USERS_COLLECTION).document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    if (profile != null) {
                        onSuccess(profile)
                    } else {
                        onFailure(Exception("Profile data is null"))
                    }
                } else {
                    // Start fresh if doc doesn't exist
                    onSuccess(UserProfile())
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting user profile", e)
                onFailure(e)
            }
    }

    /**
     * Update specific fields like settings.
     */
    fun updateUserSettings(userId: String, sosEnabled: Boolean, policeEnabled: Boolean, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val updates = mapOf(
            "sosEnabled" to sosEnabled,
            "policeEnabled" to policeEnabled
        )
        db.collection(USERS_COLLECTION).document(userId)
            .update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


    // --- Contacts Operations ---

    /**
     * Add a new contact. Enforces limit of 5 handled by caller or here.
     * We'll trust the UI to check limit, but we can also check here.
     */
    fun addContact(userId: String, contact: Contact, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Generate a new ID
        val newContactRef = db.collection(USERS_COLLECTION).document(userId).collection(CONTACTS_COLLECTION).document()
        contact.id = newContactRef.id
        
        newContactRef.set(contact)
            .addOnSuccessListener {
                Log.d(TAG, "Contact added: ${contact.name}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding contact", e)
                onFailure(e)
            }
    }

    /**
     * Update an existing contact.
     */
    fun updateContact(userId: String, contact: Contact, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (contact.id.isEmpty()) {
            onFailure(Exception("Contact ID is missing"))
            return
        }
        db.collection(USERS_COLLECTION).document(userId).collection(CONTACTS_COLLECTION).document(contact.id)
            .set(contact)
            .addOnSuccessListener {
                Log.d(TAG, "Contact updated: ${contact.name}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating contact", e)
                onFailure(e)
            }
    }

    /**
     * Delete a contact.
     */
    fun deleteContact(userId: String, contactId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(USERS_COLLECTION).document(userId).collection(CONTACTS_COLLECTION).document(contactId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Contact deleted: $contactId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error deleting contact", e)
                onFailure(e)
            }
    }

    /**
     * Get all contacts for a user.
     */
    fun getContacts(userId: String, onSuccess: (List<Contact>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(USERS_COLLECTION).document(userId).collection(CONTACTS_COLLECTION)
            .orderBy("name", Query.Direction.ASCENDING) // Optional sorting
            .get()
            .addOnSuccessListener { result ->
                val contacts = result.mapNotNull { it.toObject(Contact::class.java) }
                onSuccess(contacts)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting contacts", e)
                onFailure(e)
            }
    }
}
