package com.example.the_sos_application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.the_sos_application.databinding.FragmentTipsBinding

class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    data class Tip(val title: String, val description: String)

    private val tips = listOf(
        Tip("Share Your Location", "Always share your live location with trusted friends or family when traveling alone."),
        Tip("Be Aware of Surroundings", "Avoid distractions like using your phone while walking in isolated areas."),
        Tip("Trust Your Instincts", "If you feel uncomfortable or threatened, leave the area immediately."),
        Tip("Carry Safety Gear", "Carry a whistle, pepper spray, or other safety tools if permitted."),
        Tip("Use Well-Lit Paths", "Avoid dark alleys and take routes that are well-lit and populated."),
        Tip("Emergency Numbers on Speed Dial", "Keep local emergency numbers on speed dial for quick access."),
        Tip("Fake a Call", "If you feel followed, pretend to talk loudly on the phone to deter potential attackers."),
        Tip("Check Car Interior", "Always check the back seat of your car before getting in."),
        Tip("Avoid Oversharing Online", "Do not post your real-time location on social media."),
        Tip("Know Self-Defense", "Learning basic self-defense moves can be life-saving in emergencies.")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.rvTips.layoutManager = LinearLayoutManager(context)
        binding.rvTips.adapter = TipsAdapter(tips)
    }

    inner class TipsAdapter(private val tipList: List<Tip>) :
        RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

        inner class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
            val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.tip_item, parent, false)
            return TipViewHolder(view)
        }

        override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
            val tip = tipList[position]
            holder.tvTitle.text = tip.title
            holder.tvDescription.text = tip.description
        }

        override fun getItemCount(): Int = tipList.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
