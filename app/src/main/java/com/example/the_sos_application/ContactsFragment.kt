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
import com.example.the_sos_application.MockData.Contact

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    
    // Setup Dummy Data from MockData
    private val allContacts = ArrayList<FirestoreRepository.Contact>()
    private var displayedContacts = ArrayList<FirestoreRepository.Contact>()
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
        
        loadContacts()
        
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fabAddContact.setOnClickListener {
            if (allContacts.size >= 5) {
                Toast.makeText(context, "You can only have 5 contacts. Please delete one first.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            showAddContactDialog()
        }
    }
    
    private fun loadContacts() {
        val userId = FirebaseAuthHelper.getCurrentUserId()
        if (userId == null) return
        
        // Show loading? binding.progressBar?.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.rvContacts.visibility = View.GONE
        binding.tvEmptyState.visibility = View.GONE

        FirestoreRepository.getContacts(userId,
            onSuccess = { contacts ->
                binding.progressBar.visibility = View.GONE
                
                allContacts.clear()
                allContacts.addAll(contacts)
                
                if (allContacts.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvContacts.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvContacts.visibility = View.VISIBLE
                }
                
                filterContacts(binding.etSearch.text.toString())
                updateSelectionCount()
            },
            onFailure = { e ->
                binding.progressBar.visibility = View.GONE
                if (context != null) {
                    Toast.makeText(context, "Error loading contacts: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    
    // ... dialog code needs update to use FirestoreRepository.Contact and save/update calls ...

    // Helper to update dialog saving logic:
    // When saving:
    // FirestoreRepository.addContact/updateContact
    // On success: reload contacts or modify local list
    
    private fun showContactDialog(contactToEdit: FirestoreRepository.Contact? = null, position: Int? = null) {
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
            spPriority.setSelection(0)
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
            
            val userId = FirebaseAuthHelper.getCurrentUserId()
            if (userId == null) return@setOnClickListener

            if (contactToEdit != null && position != null) {
                // Update existing
                // Contact ID should be preserved
                val updatedContact = contactToEdit.copy(
                    name = name, 
                    number = number, 
                    priority = priority, 
                    relationship = relationship, 
                    notes = notes
                )
                
                FirestoreRepository.updateContact(userId, updatedContact,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Contact Updated", Toast.LENGTH_SHORT).show()
                        loadContacts() // Reload to be safe
                    },
                    onFailure = { e ->
                        Toast.makeText(requireContext(), "Error updating: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                // Create new
                val newContact = FirestoreRepository.Contact(
                    name = name,
                    number = number,
                    priority = priority,
                    relationship = relationship,
                    notes = notes
                )
                FirestoreRepository.addContact(userId, newContact,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Contact Added", Toast.LENGTH_SHORT).show()
                        loadContacts()
                    },
                    onFailure = { e ->
                        Toast.makeText(requireContext(), "Error adding: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
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

    private fun showViewContactDialog(contact: FirestoreRepository.Contact) {
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
    
    private fun deleteContact(contact: FirestoreRepository.Contact, position: Int) {
        val userId = FirebaseAuthHelper.getCurrentUserId() ?: return
        
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Contact")
            .setMessage("Are you sure you want to delete ${contact.name}?")
            .setPositiveButton("Delete") { _, _ ->
                FirestoreRepository.deleteContact(userId, contact.id,
                    onSuccess = {
                         Toast.makeText(requireContext(), "Contact Deleted", Toast.LENGTH_SHORT).show()
                         loadContacts()
                    },
                    onFailure = { e ->
                        Toast.makeText(requireContext(), "Error deleting: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
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
        // Selection is not in FirestoreRepository.Contact yet. 
        // We might need to handle selection locally or add it to the model if it's persistent.
        // Assuming selection is transient for SOS call, not persistent.
        // For now, I'll assume 'isSelected' was in MockData.Contact but not in FirestoreRepository.Contact.
        // I need to add 'isSelected' to FirestoreRepository.Contact but exclude it from Firestore?
        // Or just handle it in a wrapper or adapter.
        // Let's add @Exclude var isSelected: Boolean = false to FirestoreRepository.Contact
        // I need to modify FirestoreRepository.Contact first.
        val count = allContacts.count { it.isSelected }  // This will fail if isSelected is missing
        binding.tvSelectionCount.text = "Selected: $count/5"
    }

    inner class ContactsAdapter(private val contacts: List<FirestoreRepository.Contact>) :
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
                    
                    // Save selection to Firestore
                    val userId = FirebaseAuthHelper.getCurrentUserId()
                    if (userId != null) {
                        FirestoreRepository.updateContact(userId, contact,
                            onSuccess = { 
                                // Silent success or optional log
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to save selection", Toast.LENGTH_SHORT).show()
                                // Revert UI if needed, but keeping it simple for now
                            }
                        )
                    }
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
