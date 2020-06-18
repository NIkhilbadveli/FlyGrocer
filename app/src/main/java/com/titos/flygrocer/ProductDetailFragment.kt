package com.titos.flygrocer


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductDetailFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView = inflater.inflate(R.layout.fragment_product_detail, container, false)

        val ivProduct = layoutView.findViewById<ImageView>(R.id.ivProduct)
        val itemName = layoutView.findViewById<TextView>(R.id.tvItemName)
        val companyName = layoutView.findViewById<TextView>(R.id.tvCompanyName)
        val itemPrice = layoutView.findViewById<TextView>(R.id.tvItemPrice)
        val addButton = layoutView.findViewById<Button>(R.id.addToCartButton)

        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US)

        val strList = arguments?.getStringArrayList("list")!!
        val barcode = strList[4]

        Picasso.get().load(strList[0]).into(ivProduct)
        companyName.text = strList[1]
        itemName.text = strList[2]
        itemPrice.text = "\u20B9 ${strList[3]}"

        var addedToCart = false
        addButton.setOnClickListener{
            if (!addedToCart){
                userRef.child("bagItems").child(simpleDateFormat.format(Date())).setValue(barcode)
                Snackbar.make(requireView(), "Added to bag", Snackbar.LENGTH_SHORT).show()
                addButton.text = "View Bag"
                addedToCart = true
            }
            else {
                findNavController().navigate(R.id.action_productDetailsFragment_to_bagFragment)
            }

        }

        return layoutView
    }
}