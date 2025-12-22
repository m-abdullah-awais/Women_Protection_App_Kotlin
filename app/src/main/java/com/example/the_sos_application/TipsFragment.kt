package com.example.the_sos_application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class TipsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tipsContainer = view.findViewById<LinearLayout>(R.id.tipsContainer)

        val tips = listOf(
            "Share Location" to "Always let a trusted contact know where you are going when meeting someone new.",
            "Stay Aware" to "Keep your head up and stay off your phone when walking alone at night.",
            "Trust Instincts" to "If a situation feels wrong, leave immediately. Your safety comes first.",
            "Visual Checks" to "Check the back seat of your car before getting in.",
            "Emergency Keys" to "Keep your keys ready in your hand when walking to your car.",
            "Digital Privacy" to "Don't share vacation plans on social media until you return.",
            "Meet Publicly" to "Always meet strangers in public places for exchanges or dates.",
            "Battery check" to "Ensure your phone is charged before heading out.",
            "Code Word" to "Establish a code word with family for discreet emergency signaling.",
            "Route Planning" to "Stick to well-lit and populated routes."
        )

        tips.forEachIndexed { index, (title, desc) ->
            val itemView = layoutInflater.inflate(R.layout.tip_item, tipsContainer, false)
            
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTipTitle)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvTipDesc)

            tvTitle.text = title
            tvDesc.text = desc

            tipsContainer.addView(itemView)
        }
    }
}
