package com.example.the_sos_application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Helper class for Firebase Authentication.
 */
object FirebaseAuthHelper {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Get the current logged-in user.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Check if a user is logged in.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Get the current user ID. Returns null if not logged in.
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Sign out the current user.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Expose FirebaseAuth instance if needed for specific operations like creating users or signing in.
     * It's often better to wrap these, but for simplicity in Activities we might expose the instance
     * or add callback-based methods here.
     * 
     * For this implementation, we'll let Activities call signInWithEmailAndPassword directly on the auth instance
     * obtained here, or we can provide wrapper functions.
     * To keep it clean and robust as requested, I'll provide the instance getter.
     */
    fun getAuth(): FirebaseAuth {
        return auth
    }
}
