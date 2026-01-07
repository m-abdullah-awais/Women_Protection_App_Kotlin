package com.example.the_sos_application

import java.util.regex.Pattern

object ValidationUtils {

    // Email Regex: Must be @gmail.com
    private val EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]+@gmail\\.com$"
    )

    // Username Regex: Alphabetic characters only (A-Z, a-z)
    private val USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z]+$"
    )

    // Password Regex: Min 8 chars, 1 Upper, 1 Lower, 1 Number
    private val PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    )

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && EMAIL_PATTERN.matcher(email).matches()
    }

    fun isValidUsername(username: String): Boolean {
        // Check if empty is handled by caller or here. For strict validation, return false if empty.
        return username.isNotEmpty() && USERNAME_PATTERN.matcher(username).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty() && PASSWORD_PATTERN.matcher(password).matches()
    }
}
