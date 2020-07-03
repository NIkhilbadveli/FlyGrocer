package com.titos.flygrocer

import android.annotation.SuppressLint
import android.view.View
import android.widget.*

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.squareup.picasso.Picasso

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import java.text.SimpleDateFormat
import java.util.*


class OrderDetailsItem(val barcode: String, val itemQty: String): Item() {

    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)
            val productRef = FirebaseDatabase.getInstance().reference.child("/productData")
            val addToBagButton = containerView.findViewById<Button>(R.id.addToCartButton)

            //Setting visibilities
            addToBagButton.visibility = View.GONE
            containerView.findViewById<LinearLayout>(R.id.containerQty).visibility = View.VISIBLE

            productRef.child(barcode).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    val url = p0.child("URL").value.toString()
                    val companyName = p0.child("Brand").value.toString()
                    val itemName = p0.child("name").value.toString()
                    val itemPrice = p0.child("price").value.toString()

                    Picasso.get().load(url).into(ivProduct)
                    containerView.findViewById<TextView>(R.id.tvItemName).text = companyName
                    containerView.findViewById<TextView>(R.id.tvCompanyName).text = itemName
                    containerView.findViewById<TextView>(R.id.tvItemPrice).text = "\u20B9 $itemPrice"

                }
            })

            containerView.findViewById<TextView>(R.id.tvItemQty).text = itemQty
        }
    }

    override fun getLayout(): Int = R.layout.item_product
}