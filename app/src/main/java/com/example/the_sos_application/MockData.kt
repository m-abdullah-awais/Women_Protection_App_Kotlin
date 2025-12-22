package com.example.the_sos_application

object MockData {
    
    // User Profile Data
    data class UserProfile(
        var name: String,
        var email: String,
        var phone: String = "",
        var bloodType: String = ""
    )

    var currentUser = UserProfile(
        name = "John Doe",
        email = "john.doe@example.com",
        phone = "+1 234 567 890",
        bloodType = "O+"
    )

    data class Contact(
        val name: String,
        val number: String,
        var isSelected: Boolean = false,
        var priority: String = "Medium" // Critical, Urgent, High, Medium, Low
    )

    val allContacts = mutableListOf(
        Contact("Mom", "+1 555 0101", priority = "High"),
        Contact("Dad", "+1 555 0102", priority = "High"),
        Contact("Best Friend", "+1 555 0103"),
        Contact("Sister", "+1 555 0104"),
        Contact("Work Emergency", "+1 555 0105", priority = "Low"),
        Contact("Neighbour", "+1 555 0106", priority = "Low"),
        Contact("Doctor", "+1 555 0107", priority = "Critical"),
        Contact("Police Station", "911", priority = "Critical"),
        Contact("Fire Corp", "911", priority = "Urgent"),
        Contact("School Admin", "+1 555 0110")
    )

    fun getSelectedCount(): Int {
        return allContacts.count { it.isSelected }
    }

    // Helper: Return contacts sorted ALPHABETICALLY ONLY (User request: No jump to top)
    fun getSortedContacts(): List<Contact> {
        return allContacts.sortedBy { it.name }
    }

    fun addContact(name: String, number: String) {
        allContacts.add(Contact(name, number))
    }

    fun getSOSSummary(): String {
        val selected = allContacts.filter { it.isSelected }
        if (selected.isEmpty()) return "No contacts selected!"

        val priorityOrder = listOf("Critical", "Urgent", "High", "Medium", "Low")
        val sorted = selected.sortedBy { priorityOrder.indexOf(it.priority) }
        
        val namesByPriority = sorted.joinToString(" → ") { "${it.name} (${it.priority})" }
        return "Notifying: $namesByPriority"
    }

    fun resetSelection() {
        allContacts.forEach { it.isSelected = false }
    }
}
