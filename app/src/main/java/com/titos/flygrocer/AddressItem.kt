package com.titos.flygrocer

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

import kotlinx.android.synthetic.main.item_profile.*

class AddressItem(private val addressId: Int, val addressLine1: String, val addressLine2: String, val addressLine3: String,
                  val mobileNumber: String, private val openEditAddress: ((Int) -> Unit), val groupAdapter: GroupAdapter<com.xwray.groupie.GroupieViewHolder>): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {
            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

            containerView.findViewById<TextView>(R.id.tvAddressLine1).text = addressLine1
            containerView.findViewById<TextView>(R.id.tvAddressLine1).text = addressLine2
            containerView.findViewById<TextView>(R.id.tvAddressLine1).text = addressLine3
            containerView.findViewById<TextView>(R.id.tvMobileNumber).text = mobileNumber

            containerView.findViewById<Button>(R.id.editAddressButton).setOnClickListener { openEditAddress.invoke(addressId) }

            containerView.findViewById<Button>(R.id.removeAddressButton).setOnClickListener {
                groupAdapter.removeGroupAtAdapterPosition(position)
                userRef.child("addresses").child(addressId.toString()).removeValue()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_order
}