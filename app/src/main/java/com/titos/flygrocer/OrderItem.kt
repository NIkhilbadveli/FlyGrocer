package com.titos.flygrocer

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile.*

class OrderItem(private val orderNumber: String,
                private val openOrderDetails: ((String) -> Unit)): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {
            val orderRef = FirebaseDatabase.getInstance().reference.child("/allOrders")

            orderRef.child(orderNumber).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    containerView.findViewById<TextView>(R.id.tvOrderNumber).text = orderNumber
                    containerView.findViewById<TextView>(R.id.tvOrderQty).text = p0.child("orderQty").value.toString()
                    containerView.findViewById<TextView>(R.id.tvOrderTotal).text = p0.child("orderTotal").value.toString()
                    containerView.findViewById<TextView>(R.id.tvOrderDate).text = p0.child("orderDate").value.toString()
                    containerView.findViewById<TextView>(R.id.tvOrderStatus).text = p0.child("orderStatus").value.toString()
                }
            })

            containerView.findViewById<Button>(R.id.orderDetailsButton).setOnClickListener { openOrderDetails.invoke(orderNumber) }
        }
    }

    override fun getLayout(): Int = R.layout.item_order
}