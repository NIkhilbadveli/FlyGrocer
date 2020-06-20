package com.titos.flygrocer

import android.annotation.SuppressLint
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class FavItem(val barcode: String): Item() {
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)

            val productRef = FirebaseDatabase.getInstance().reference.child("/productData")

            val tvItemName = containerView.findViewById<TextView>(R.id.tvItemName)
            val tvCompanyName = containerView.findViewById<TextView>(R.id.tvCompanyName)
            val tvItemPrice = containerView.findViewById<TextView>(R.id.tvItemPrice)

            productRef.child(barcode).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val url = p0.child("URL").value.toString()
                    val companyName = p0.child("Brand").value.toString()
                    val itemName = p0.child("name").value.toString()
                    val itemPrice = p0.child("price").value.toString()

                    Picasso.get().load(url).into(ivProduct)
                    tvCompanyName.text = companyName
                    tvItemName.text = itemName
                    tvItemPrice.text = itemPrice

                }
            })

        }
    }

    override fun getLayout(): Int = R.layout.item_product

}