package com.titos.flygrocer

import android.annotation.SuppressLint
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
import java.util.*

class OrderItem(private val orderNumber: String, private val cityName: String,
                private val openOrderDetails: ((String) -> Unit)): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {
            val orderRef = FirebaseDatabase.getInstance().reference.child("/allOrders")

            orderRef.child(cityName.toUpperCase(Locale.ROOT)).child(orderNumber).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                @SuppressLint("SetTextI18n")
                override fun onDataChange(p0: DataSnapshot) {
                    containerView.findViewById<TextView>(R.id.tvOrderTotal).text = "\u20B9 ${p0.child("orderTotal").value.toString()}"
                    containerView.findViewById<TextView>(R.id.tvOrderDate).text = p0.child("orderTime").value.toString()
                    val status = p0.child("orderStatus").value.toString()
                    containerView.findViewById<TextView>(R.id.tvOrderStatus).text = status
                    if (status=="Delivered")
                        containerView.findViewById<TextView>(R.id.tvOrderStatus).setTextColor(containerView.context.resources
                            .getColor(R.color.green))

                }
            })

            containerView.setOnClickListener { openOrderDetails.invoke(orderNumber) }
        }
    }

    override fun getLayout(): Int = R.layout.item_order
}