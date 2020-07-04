package com.titos.flygrocer

import android.annotation.SuppressLint
import android.media.Image
import android.view.View
import android.widget.*

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.squareup.picasso.Picasso

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import java.text.SimpleDateFormat
import java.util.*


class ProductItem(var addedTime:String, val barcode: String, val imageUrl: String, val companyName: String, val itemName: String, val itemPrice: String,
val onItemClick: ((ProductItem)-> Unit), var presentinBag: Boolean): Item() {
    var itemQuantity = "1"
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)
            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
            val addToBagButton = containerView.findViewById<Button>(R.id.addToCartButton)
            val plusMinusContainer = containerView.findViewById<LinearLayout>(R.id.plusMinusContainer)
            val tvItemPrice = containerView.findViewById<TextView>(R.id.tvItemPrice)
            val editTextQty = containerView.findViewById<EditText>(R.id.itemQty)

            editTextQty.setText(itemQuantity)

            //Setting visibilities
            if (presentinBag) {
                plusMinusContainer.visibility = View.VISIBLE
                addToBagButton.visibility = View.GONE
            }
            else{
                plusMinusContainer.visibility = View.GONE
                addToBagButton.visibility = View.VISIBLE
            }

            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US)

            containerView.findViewById<TextView>(R.id.tvItemName).text = itemName
            containerView.findViewById<TextView>(R.id.tvCompanyName).text = companyName
            tvItemPrice.text = "\u20B9 $itemPrice"

            containerView.findViewById<RelativeLayout>(R.id.rlContainer).setOnClickListener { onItemClick.invoke(this@ProductItem) }
            ivProduct.setOnClickListener { onItemClick.invoke(this@ProductItem) }
            Picasso.get().load(imageUrl).into(ivProduct)

            //Handling adding to bag
            addToBagButton.setOnClickListener {
                Toast.makeText(containerView.context,"Added to Bag", Toast.LENGTH_SHORT).show()
                addedTime = simpleDateFormat.format(Date())
                userRef.child("bagItems").child(addedTime).child("barcode").setValue(barcode)
                userRef.child("bagItems").child(addedTime).child("qty").setValue(1)
                /*plusMinusContainer.visibility = View.VISIBLE
                addToBagButton.visibility = View.GONE*/
                presentinBag = true
                notifyChanged()
            }

            //Handling minus for quantity
            containerView.findViewById<ImageButton>(R.id.subtractQuantityFab).setOnClickListener {
                val currentQty = editTextQty.text.toString().toInt()
                if (currentQty>1){
                    val updatedQty = currentQty - 1
                    editTextQty.setText(updatedQty.toString())
                    //tvItemPrice.text = "\u20B9 ${(updatedQty*itemPrice.toInt())}"
                    userRef.child("bagItems").child(addedTime).child("qty").setValue(updatedQty)
                    itemQuantity = updatedQty.toString()
                    notifyChanged()
                }
            }

            //Handling plus for quantity
            containerView.findViewById<ImageButton>(R.id.addQuantityFab).setOnClickListener {
                val updatedQty = editTextQty.text.toString().toInt() + 1
                editTextQty.setText(updatedQty.toString())
                //tvItemPrice.text = "\u20B9 ${(updatedQty*itemPrice.toInt())}"
                userRef.child("bagItems").child(addedTime).child("qty").setValue(updatedQty)
                itemQuantity = updatedQty.toString()
                notifyChanged()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_product
}