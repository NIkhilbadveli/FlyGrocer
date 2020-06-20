package com.titos.flygrocer

import android.annotation.SuppressLint
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

class FavItem(val barcode: String, var presentinBag: Boolean, var addedTime: String, val onItemClick: ((FavItem)-> Unit)): Item() {
    var url = ""
    var companyName = ""
    var itemName = ""
    var itemPrice = ""
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)
            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
            val productRef = FirebaseDatabase.getInstance().reference.child("/productData")

            val fab = containerView.findViewById<FloatingActionButton>(R.id.addToBagFab)
            val tvItemName = containerView.findViewById<TextView>(R.id.tvItemName)
            val tvCompanyName = containerView.findViewById<TextView>(R.id.tvCompanyName)
            val tvItemPrice = containerView.findViewById<TextView>(R.id.tvItemPrice)
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US)

            var added = presentinBag

            if (presentinBag)
                fab.setImageResource(R.drawable.ic_baseline_shopping_basket_24)

            ivProduct.setOnClickListener { onItemClick.invoke(this@FavItem) }

            productRef.child(barcode).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    url = p0.child("URL").value.toString()
                    companyName = p0.child("Brand").value.toString()
                    itemName = p0.child("name").value.toString()
                    itemPrice = p0.child("price").value.toString()

                    Picasso.get().load(url).into(ivProduct)
                    tvCompanyName.text = companyName
                    tvItemName.text = itemName
                    tvItemPrice.text = itemPrice
                }
            })

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