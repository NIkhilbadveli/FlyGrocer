package com.titos.flygrocer

import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.squareup.picasso.Picasso

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import java.text.SimpleDateFormat
import java.util.*


class ProductItem(val barcode: String, val imageUrl: String, val companyName: String, val itemName: String, val itemPrice: String,
val onItemClick: ((ProductItem)-> Unit)): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)
            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US)
            var added = false
            var addedTime = "00-00-0000 00:00:00"

            containerView.findViewById<TextView>(R.id.tvItemName).text = itemName
            containerView.findViewById<TextView>(R.id.tvCompanyName).text = companyName
            containerView.findViewById<TextView>(R.id.tvItemPrice).text = itemPrice

            ivProduct.setOnClickListener { onItemClick.invoke(this@ProductItem) }
            Picasso.get().load(imageUrl).into(ivProduct)

            val fab = containerView.findViewById<FloatingActionButton>(R.id.addToBagFab)
            fab.setOnClickListener {
                if(!added){
                    Toast.makeText(containerView.context,"Added to Bag", Toast.LENGTH_SHORT).show()
                    addedTime = simpleDateFormat.format(Date())
                    userRef.child("bagItems").child(addedTime).setValue(barcode)
                    fab.setImageResource(R.drawable.ic_baseline_shopping_basket_24)
                    added = true
                }
                else {
                    Toast.makeText(containerView.context,"Removed from Bag", Toast.LENGTH_SHORT).show()
                    userRef.child("bagItems").child(addedTime).removeValue()
                    fab.setImageResource(R.drawable.ic_outline_shopping_basket_24)
                    added = false
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_product
}