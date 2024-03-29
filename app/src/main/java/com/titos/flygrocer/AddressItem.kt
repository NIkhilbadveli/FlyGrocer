package com.titos.flygrocer

import android.view.View
import android.widget.*
import androidx.core.content.contentValuesOf
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
                  val mobileNumber: String, private val openEditAddress: ((Int) -> Unit),
                  val groupAdapter: GroupAdapter<com.xwray.groupie.GroupieViewHolder>, val insideCheckout: Boolean,
                  val handleRadioButton: ((Int)->Unit),
                  val showEmptyContainer: (()->Unit)): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {
            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")


            if (insideCheckout) {
                containerView.findViewById<Button>(R.id.editAddressButton).visibility = View.GONE
                containerView.findViewById<Button>(R.id.removeAddressButton).visibility = View.GONE
                containerView.findViewById<TextView>(R.id.tvEdit).visibility = View.VISIBLE
                containerView.findViewById<RadioButton>(R.id.radioAddress).visibility = View.VISIBLE
                containerView.findViewById<RelativeLayout>(R.id.containerAddress).setOnClickListener { handleRadioButton.invoke(position) }
            }

            containerView.findViewById<TextView>(R.id.tvAddressLine1).text = addressLine1
            containerView.findViewById<TextView>(R.id.tvAddressLine2).text = addressLine2
            containerView.findViewById<TextView>(R.id.tvAddressLine3).text = addressLine3
            containerView.findViewById<TextView>(R.id.tvMobileNumber).text = mobileNumber

            containerView.findViewById<Button>(R.id.editAddressButton).setOnClickListener { openEditAddress.invoke(addressId) }

            containerView.findViewById<Button>(R.id.removeAddressButton).setOnClickListener {
                groupAdapter.removeGroupAtAdapterPosition(position)
                if (groupAdapter.itemCount==0)
                    showEmptyContainer.invoke()
                userRef.child("addresses").child(addressId.toString()).removeValue()
            }

            containerView.findViewById<TextView>(R.id.tvEdit).setOnClickListener { openEditAddress.invoke(addressId)  }

            if (position==0)
                containerView.findViewById<RadioButton>(R.id.radioAddress).isChecked = true
            containerView.findViewById<RadioButton>(R.id.radioAddress).setOnClickListener { handleRadioButton.invoke(position) }
        }
    }

    override fun getLayout(): Int = R.layout.item_address
}