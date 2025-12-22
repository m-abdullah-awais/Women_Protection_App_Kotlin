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
        var priority: String = "Low"
    )

    private val allContacts = mutableListOf<Contact>(
        Contact("Mom", "+1234567890"),
        Contact("Dad", "+1987654321"),
        Contact("Police Station", "100"),
        Contact("Sister", "+1122334455"),
        Contact("Brother", "+1554433221"),
        Contact("Best Friend", "+1998877665"),
        Contact("Office Security", "+1445566778"),
        Contact("Neighbor", "+1223344556"),
        Contact("Doctor", "+1777888999"),
        Contact("Emergency Hotline", "911")
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

    private fun showPriorityDialog(contact: Contact, position: Int) {
        val priorities = arrayOf("Critical", "Urgent", "High", "Medium", "Low")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Priority for ${contact.name}")
            .setItems(priorities) { _, which ->
                contact.priority = priorities[which]
                adapter.notifyItemChanged(position)
            }
            .show()
    }

    inner class ContactsAdapter(private val contacts: List<Contact>) :
        RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvName: TextView = itemView.findViewById(R.id.tvName)
            val tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
            val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
            val cbSelect: CheckBox = itemView.findViewById(R.id.cbSelect)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_item, parent, false)
            return ContactViewHolder(view)
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val contact = contacts[position]
            holder.tvName.text = contact.name
            holder.tvNumber.text = contact.number
            holder.tvPriority.text = contact.priority
            
            // Remove listener to avoid cyclical triggers during scrolling
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

            holder.itemView.setOnClickListener {
                if (contact.isSelected) {
                    showPriorityDialog(contact, position)
                } else {
                    Toast.makeText(context, "Select contact to change priority", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getItemCount(): Int = contacts.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
