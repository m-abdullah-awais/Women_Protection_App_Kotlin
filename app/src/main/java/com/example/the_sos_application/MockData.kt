package com.example.the_sos_application

object MockData {
    data class UserProfile(
        var name: String = "Jane Doe",
        var email: String = "jane.doe@example.com",
        var phone: String = "+1234567890",
        var bloodType: String = "O+",
        var age: String = "25",
        var medicalConditions: String = "None",
        var emergencyNotes: String = "Allergic to Penicillin"
    )

    data class Contact(
        val name: String, 
        val number: String, 
        var isSelected: Boolean = false, 
        var priority: String = "Low",
        val relationship: String = "",
        val notes: String = ""
    )

    var currentUser = UserProfile()

    // Centralized Emergency Contacts List
    val emergencyContactsList = mutableListOf<Contact>(
        Contact("Mom", "+923027844080", isSelected = true, relationship = "Mother", priority = "Critical"),
        Contact("Dad", "+923040330944", isSelected = true, relationship = "Father", priority = "Urgent"),
        Contact("Sister", "+1122334455", isSelected = false, relationship = "Sister", priority = "Medium"),
        Contact("Brother", "+1554433221", isSelected = false, relationship = "Brother", priority = "Medium"),
        Contact("Best Friend", "+1998877665", isSelected = false, relationship = "Friend", priority = "Low"),
        Contact("Neighbor", "+1223344556", isSelected = false, relationship = "Neighbor", priority = "Low"),
        Contact("Doctor", "+1777888999", isSelected = false, relationship = "Doctor", priority = "Low")
    )

    // Helper to map priority string to integer for sorting (Lower is higher priority)
    fun getPriorityValue(priority: String): Int {
        return when (priority) {
            "Critical" -> 1
            "Urgent" -> 2
            "High" -> 3
            "Medium" -> 4
            "Low" -> 5
            else -> 6
        }
    }
}
