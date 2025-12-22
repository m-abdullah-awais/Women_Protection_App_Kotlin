package com.example.the_sos_application

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class ContactsFragment : Fragment() {

    private lateinit var contactsContainer: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var tvCounter: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        contactsContainer = view.findViewById(R.id.contactsContainer)
        etSearch = view.findViewById(R.id.etSearchContacts)
        tvCounter = view.findViewById(R.id.tvSelectionCounter)
        val btnAdd = view.findViewById<ImageButton>(R.id.btnAddContactHeader)

        updateCounter() 
        contactsContainer.post { renderContacts("") }

        btnAdd.setOnClickListener { showAddContactDialog() }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                renderContacts(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun renderContacts(query: String) {
        contactsContainer.removeAllViews()
        val sortedList = MockData.getSortedContacts()
        val filteredContacts = sortedList.filter { it.name.contains(query, ignoreCase = true) }

        val context = requireContext()
        val inflater = LayoutInflater.from(context)

        for (contact in filteredContacts) {
            val itemView = inflater.inflate(R.layout.contact_item, contactsContainer, false)
            
            val tvName: TextView = itemView.findViewById(R.id.tvName)
            val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
            val spinner: Spinner = itemView.findViewById(R.id.spinnerPriority)
            val checkBox: CheckBox = itemView.findViewById(R.id.cbSelect)

            tvName.text = contact.name
            tvNumber.text = contact.number
            
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = contact.isSelected
            spinner.isEnabled = contact.isSelected

             val priorities = listOf("Critical", "Urgent", "High", "Medium", "Low")
             val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, priorities)
             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
             spinner.adapter = adapter
            
            // Set spinner selection without triggering listener if possible, strictly by index
            val priorityIndex = priorities.indexOf(contact.priority)
            if (priorityIndex >= 0) spinner.setSelection(priorityIndex, false)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    contact.priority = priorities[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (MockData.getSelectedCount() >= 5) {
                        checkBox.isChecked = false
                        Toast.makeText(context, "Max 5 contacts allowed!", Toast.LENGTH_SHORT).show()
                    } else {
                        contact.isSelected = true
                        spinner.isEnabled = true
                        updateCounter()
                    }
                } else {
                    contact.isSelected = false
                    spinner.isEnabled = false
                    updateCounter()
                }
            }
            contactsContainer.addView(itemView)
        }
    }

    private fun showAddContactDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_contact, null)
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Transparent for rounded corners

        val etName = dialogView.findViewById<EditText>(R.id.etNewContactName)
        val etNumber = dialogView.findViewById<EditText>(R.id.etNewContactNumber)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveContact)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelContact)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val number = etNumber.text.toString()
            if (name.isNotEmpty() && number.isNotEmpty()) {
                MockData.addContact(name, number)
                renderContacts(etSearch.text.toString())
                Toast.makeText(requireContext(), "Contact Added", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun updateCounter() {
        val count = MockData.getSelectedCount()
        tvCounter.text = "Selected: $count/5"
    }
}
