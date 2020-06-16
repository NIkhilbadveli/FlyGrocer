package com.titos.flygrocer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class BagFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView =  inflater.inflate(R.layout.fragment_bag, container, false)

        val checkoutButton = layoutView.findViewById<Button>(R.id.checkoutButton)
        checkoutButton.setOnClickListener{
            findNavController().navigate(R.id.action_bagFragment_to_checkoutFragment)
        }

        return layoutView
    }
}