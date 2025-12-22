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

    var currentUser = UserProfile()
}
