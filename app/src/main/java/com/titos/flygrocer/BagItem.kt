package com.titos.flygrocer

import android.annotation.SuppressLint
import android.view.MenuItem
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item


class BagItem(val addedTime: String, val barcode: String, var itemQty: String,
              val tvTotal: TextView, val adapter: GroupAdapter<com.xwray.groupie.GroupieViewHolder>): Item() {
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)
            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
            val productRef = FirebaseDatabase.getInstance().reference.child("/productData")

            val tvItemName = containerView.findViewById<TextView>(R.id.tvItemName)
            val tvCompanyName = containerView.findViewById<TextView>(R.id.tvCompanyName)
            val tvItemPrice = containerView.findViewById<TextView>(R.id.tvItemPrice)
            val editTextQty = containerView.findViewById<EditText>(R.id.itemQty)
            val threeDots = containerView.findViewById<ImageButton>(R.id.moreOptionsMenu)
            var unitPrice = 0
            var totalCost = 0

            //Adding initial quantity of 1
            editTextQty.setText(itemQty)

            productRef.child(barcode).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val initialTotal = tvTotal.text.toString().split(" ").last().toInt()
                    val url = p0.child("URL").value.toString()
                    val companyName = p0.child("Brand").value.toString()
                    val itemName = p0.child("name").value.toString()
                    val itemPrice = p0.child("price").value.toString()

                    Picasso.get().load(url).into(ivProduct)
                    tvCompanyName.text = companyName
                    tvItemName.text = itemName
                    totalCost = itemQty.toInt()*itemPrice.toInt()
                    tvItemPrice.text = "\u20B9 $totalCost"
                    unitPrice = itemPrice.toInt()
                    tvTotal.text =  "\u20B9 ${(initialTotal + totalCost)}"
                }
            })

            //Handling minus for quantity
            containerView.findViewById<FloatingActionButton>(R.id.subtractQuantityFab).setOnClickListener {
                val currentQty = editTextQty.text.toString().toInt()
                val currentTotal = tvTotal.text.toString().split(" ").last().toInt()
                if (currentQty>1){
                    val updatedQty = currentQty - 1
                    editTextQty.setText(updatedQty.toString())
                    tvItemPrice.text = "\u20B9 ${(updatedQty*unitPrice)}"
                    tvTotal.text = "\u20B9 ${(currentTotal + totalCost)}"
                    itemQty = updatedQty.toString()
                    notifyChanged()
                }
            }

            //Handling plus for quantity
            containerView.findViewById<FloatingActionButton>(R.id.addQuantityFab).setOnClickListener {
                val currentTotal = tvTotal.text.toString().split(" ").last().toInt()
                val updatedQty = editTextQty.text.toString().toInt() + 1
                editTextQty.setText(updatedQty.toString())
                tvItemPrice.text = "\u20B9 ${(updatedQty*unitPrice)}"
                tvTotal.text = "\u20B9 ${(currentTotal - totalCost)}"
                itemQty = updatedQty.toString()
                notifyChanged()
            }

            //creating a popup menu
            val popup = PopupMenu(containerView.context, threeDots)
            popup.inflate(R.menu.delete_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.deleteOption -> {
                        val currentTotal = tvTotal.text.toString().split(" ").last().toInt()
                        val price = tvItemPrice.text.toString().split(" ").last().toInt()
                        adapter.removeGroupAtAdapterPosition(adapterPosition)
                        Toast.makeText(containerView.context,"Removed from Bag", Toast.LENGTH_SHORT).show()
                        userRef.child("bagItems").child(addedTime).removeValue()
                        tvTotal.text = "\u20B9 ${(currentTotal - price)}"
                    }
                }
                false
            }

            //Adding delete option
            threeDots.setOnClickListener {
                popup.show()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_bag
}