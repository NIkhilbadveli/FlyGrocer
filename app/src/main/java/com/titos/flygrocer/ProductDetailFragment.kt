package com.titos.flygrocer


import android.annotation.SuppressLint
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductDetailFragment: Fragment() {

    @SuppressLint("SetTextI18n")
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
        val favIcon = layoutView.findViewById<FloatingActionButton>(R.id.fav_icon)
        
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US)
        var addedTime = "00-00-0000 00:00:00"
        val strList = arguments?.getStringArrayList("list")!!
        val barcode = strList[4]

        Picasso.get().load(strList[0]).into(ivProduct)
        companyName.text = strList[1]
        itemName.text = strList[2]
        itemPrice.text = "\u20B9 ${strList[3]}"

        var addedToCart = strList[5]!!.toBoolean()
        if (addedToCart)
            addButton.text = "View in Bag"

        addButton.setOnClickListener{
            if (!addedToCart){
                addedTime = simpleDateFormat.format(Date())
                userRef.child("bagItems").child(addedTime).child("barcode").setValue(barcode)
                userRef.child("bagItems").child(addedTime).child("qty").setValue(1)
                Snackbar.make(requireView(), "Added to bag", Snackbar.LENGTH_SHORT).show()
                addButton.text = "View in Bag"
                addedToCart = true
            }
            else {
                findNavController().navigate(R.id.action_productDetailsFragment_to_bagFragment)
                //findNavController().popBackStack(R.id.productDetailsFragment, false)
            }

        }

        var addToFav = false

        //Checking if the current barcode is present in favItems
        userRef.child("favItems").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(timeStamp in p0.children){
                    if (barcode == timeStamp.value.toString()) {
                        addToFav = true
                        favIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
                    }
                }

            }
        })

        favIcon.setOnClickListener {
            if(!addToFav){
                addedTime = simpleDateFormat.format(Date())
                userRef.child("favItems").child(addedTime).setValue(barcode)
                Snackbar.make(requireView(),"Added to My favourits",Snackbar.LENGTH_SHORT).show()
                favIcon.setImageResource(R.drawable.ic_baseline_favorite_24)
                addToFav = true
            }
            else{
                userRef.child("favItems").child(addedTime).removeValue()
                Snackbar.make(requireView(), "Removed..", Snackbar.LENGTH_SHORT).show()
                favIcon.setImageResource(R.drawable.ic_outline_favorite_border_24)
                addToFav = false
            }
        }

        return layoutView
    }

}