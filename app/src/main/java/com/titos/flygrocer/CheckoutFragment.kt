package com.titos.flygrocer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class CheckoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView =  inflater.inflate(R.layout.fragment_checkout, container, false)

        val paymentButton = layoutView.findViewById<Button>(R.id.paymentButton)

        paymentButton.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_trackingFragment)
        }

        return layoutView
    }
}