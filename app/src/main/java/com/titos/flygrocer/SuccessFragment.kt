package com.titos.flygrocer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class SuccessFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val layoutView = inflater.inflate(R.layout.fragment_success, container, false)

        val orderId = arguments?.getString("orderId")!!

        layoutView.findViewById<TextView>(R.id.tvOrderId).text = "#${orderId}"

        layoutView.findViewById<Button>(R.id.trackButton).setOnClickListener {
            findNavController().navigate(R.id.action_successFragment_to_trackingFragment)
        }
        return layoutView
    }
}