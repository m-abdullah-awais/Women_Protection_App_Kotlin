package com.example.the_sos_application

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.the_sos_application.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    
    // Setup Dummy Data
    data class Contact(
        val name: String, 
        val number: String, 
        var isSelected: Boolean = false, 
        var priority: String = "Low",
        val relationship: String = "",
        val notes: String = ""
    )

    private val allContacts = mutableListOf<Contact>(
        Contact("Mom", "+1234567890", relationship = "Mother", priority = "High"),
        Contact("Dad", "+1987654321", relationship = "Father", priority = "High"),
        Contact("Police Station", "100", relationship = "Emergency", priority = "Critical"),
        Contact("Sister", "+1122334455", relationship = "Sister", priority = "Medium"),
        Contact("Brother", "+1554433221", relationship = "Brother", priority = "Medium"),
        Contact("Best Friend", "+1998877665", relationship = "Friend", priority = "Low"),
        Contact("Office Security", "+1445566778", relationship = "Security", priority = "Low"),
        Contact("Neighbor", "+1223344556", relationship = "Neighbor", priority = "Low"),
        Contact("Doctor", "+1777888999", relationship = "Doctor", priority = "Urgent"),
        Contact("Emergency Hotline", "911", relationship = "Emergency", priority = "Critical")
    )
    
    private var displayedContacts = ArrayList<Contact>(allContacts)
    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactsAdapter(displayedContacts)
        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.adapter = adapter
        
        updateSelectionCount()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fabAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun showContactDialog(contactToEdit: Contact? = null, position: Int? = null) {
        val title = if (contactToEdit == null) "Add Contact" else "Edit Contact"
        val btnText = if (contactToEdit == null) "Add" else "Save"

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
            
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvAddContactTitle)
        val etName = dialogView.findViewById<TextView>(R.id.etNewContactName)
        val etNumber = dialogView.findViewById<TextView>(R.id.etNewContactNumber)
        val etRelationship = dialogView.findViewById<TextView>(R.id.etRelationship)
        val spPriority = dialogView.findViewById<android.widget.Spinner>(R.id.spPriority)
        val etNotes = dialogView.findViewById<TextView>(R.id.etNotes)
        val btnSave = dialogView.findViewById<TextView>(R.id.btnSaveContact)
        val btnCancel = dialogView.findViewById<View>(R.id.btnCancelContact)
        
        tvTitle.text = title
        btnSave.text = btnText

        // Setup Spinner
        val priorities = arrayOf("Low", "Medium", "High", "Urgent", "Critical")
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPriority.adapter = adapter
        
        // Pre-fill if editing
        if (contactToEdit != null) {
            etName.text = contactToEdit.name
            etNumber.text = contactToEdit.number
            etRelationship.text = contactToEdit.relationship
            etNotes.text = contactToEdit.notes
            val priorityIndex = priorities.indexOf(contactToEdit.priority)
            if (priorityIndex >= 0) spPriority.setSelection(priorityIndex)
        } else {
            spPriority.setSelection(0) // Default Low
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val number = etNumber.text.toString().trim()
            val relationship = etRelationship.text.toString().trim()
            val priority = spPriority.selectedItem.toString()
            val notes = etNotes.text.toString().trim()

            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(requireContext(), "Name and Number are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contactToEdit != null && position != null) {
                // Update existing
                val updatedContact = contactToEdit.copy(
                    name = name, 
                    number = number, 
                    priority = priority, 
                    relationship = relationship, 
                    notes = notes
                )
                // Note: direct update on reference logic depends on if displayedContacts holds refs or copies.
                // Here we update the list directly.
                allContacts[allContacts.indexOf(contactToEdit)] = updatedContact
                displayedContacts[position] = updatedContact
                this.adapter.notifyItemChanged(position)
                Toast.makeText(requireContext(), "Contact Updated", Toast.LENGTH_SHORT).show()
            } else {
                // Create new
                val newContact = Contact(name, number, false, priority, relationship, notes)
                allContacts.add(0, newContact)
                filterContacts(binding.etSearch.text.toString())
                binding.rvContacts.smoothScrollToPosition(0)
                Toast.makeText(requireContext(), "Contact Added", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    
    // Alias for Add button
    private fun showAddContactDialog() {
        showContactDialog(null, null)
    }

    private fun showViewContactDialog(contact: Contact) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_view_contact, null)
        val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.tvViewName).text = contact.name
        dialogView.findViewById<TextView>(R.id.tvViewRelationship).text = if(contact.relationship.isNotEmpty()) contact.relationship else "No relationship specified"
        dialogView.findViewById<TextView>(R.id.tvViewNumber).text = contact.number
        dialogView.findViewById<TextView>(R.id.tvViewPriority).text = contact.priority
        dialogView.findViewById<TextView>(R.id.tvViewNotes).text = if(contact.notes.isNotEmpty()) contact.notes else "No notes."

        dialogView.findViewById<View>(R.id.btnCloseView).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    
    private fun deleteContact(contact: Contact, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                allContacts.remove(contact) // Remove from source
                displayedContacts.removeAt(position) // Remove from view
                adapter.notifyItemRemoved(position)
                updateSelectionCount()
                Toast.makeText(requireContext(), "Contact Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun filterContacts(query: String) {
        displayedContacts.clear()
        if (query.isEmpty()) {
            displayedContacts.addAll(allContacts)
        } else {
            val lowerQuery = query.lowercase()
            for (contact in allContacts) {
                if (contact.name.lowercase().contains(lowerQuery)) {
                    displayedContacts.add(contact)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun updateSelectionCount() {
        val count = allContacts.count { it.isSelected }
        binding.tvSelectionCount.text = "Selected: $count/5"
    }

    inner class ContactsAdapter(private val contacts: List<Contact>) :
        RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tvName)
            val tvRelationship: TextView = itemView.findViewById(R.id.tvRelationship)
            val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
            val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
            val cbSelect: CheckBox = itemView.findViewById(R.id.cbSelect)
            val btnMore: android.widget.ImageView = itemView.findViewById(R.id.btnMore)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_item, parent, false)
            return ContactViewHolder(view)
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val contact = contacts[position]
            holder.tvName.text = contact.name
            holder.tvRelationship.text = contact.relationship
            holder.tvNumber.text = contact.number
            holder.tvPriority.text = contact.priority
            
            // Handle Checkbox
            holder.cbSelect.setOnCheckedChangeListener(null)
            holder.cbSelect.isChecked = contact.isSelected

            holder.cbSelect.setOnCheckedChangeListener { _, isChecked ->
                val currentSelectionCount = allContacts.count { it.isSelected }
                if (isChecked && currentSelectionCount >= 5 && !contact.isSelected) {
                    holder.cbSelect.isChecked = false
                    Toast.makeText(context, "Limit reached! You can only select 5 contacts.", Toast.LENGTH_SHORT).show()
                } else {
                    contact.isSelected = isChecked
                    updateSelectionCount()
                }
            }
            
            // Handle Menu
            holder.btnMore.setOnClickListener { view ->
                val popup = android.widget.PopupMenu(context, view)
                popup.menu.add("View")
                popup.menu.add("Edit")
                popup.menu.add("Delete")
                
                popup.setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "View" -> showViewContactDialog(contact)
                        "Edit" -> showContactDialog(contact, position)
                        "Delete" -> deleteContact(contact, position)
                    }
                    true
                }
                popup.show()
            }

            // Optional: Click on item to View
            holder.itemView.setOnClickListener {
               showViewContactDialog(contact)
            }
        }

        override fun getItemCount(): Int = contacts.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
