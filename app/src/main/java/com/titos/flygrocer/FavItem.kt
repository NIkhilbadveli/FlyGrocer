package com.titos.flygrocer

import android.annotation.SuppressLint
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import java.text.SimpleDateFormat
import java.util.*

class FavItem(val barcode: String, var presentinBag: Boolean, var addedTime: String, val adapter: GroupAdapter<com.xwray.groupie.GroupieViewHolder>,
              val onItemClick: ((FavItem)-> Unit),val showEmptyContainer: (()->Unit)): Item() {
    var url = ""
    var companyName = ""
    var itemName = ""
    var itemPrice = ""
    var bagAddedTime = "00-00-0000 00:00:00"
    @SuppressLint("SetTextI18n")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)
            val threeDots = containerView.findViewById<ImageButton>(R.id.deleteMenu)
            val user = FirebaseAuth.getInstance().currentUser!!
            val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

            var shopType = "superGrocer"

            if(barcode.first()=='1'&&barcode.length==4)
                shopType = "looseItems"
            else if (barcode.first()=='2'&&barcode.length==4)
                shopType = "greenGrocer"
            else if (barcode.first()=='3'&&barcode.length==4)
                shopType = "foodGrocer"

            val bagType = if (shopType=="looseItems") "superBag" else shopType.replace("Grocer", "Bag")

            val productRef = FirebaseDatabase.getInstance().reference.child("/productData/$shopType/$barcode")

            val fab = containerView.findViewById<FloatingActionButton>(R.id.addToBagFab)
            val tvItemName = containerView.findViewById<TextView>(R.id.tvItemName)
            val tvCompanyName = containerView.findViewById<TextView>(R.id.tvCompanyName)
            val tvItemPrice = containerView.findViewById<TextView>(R.id.tvItemPrice)
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.US)

            if (presentinBag)
                fab.setImageResource(R.drawable.ic_baseline_shopping_basket_24)

            ivProduct.setOnClickListener { onItemClick.invoke(this@FavItem) }

            productRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    url = p0.child(if(shopType == "superGrocer" || shopType == "looseItems") "URL" else "url").value.toString()
                    companyName = when (shopType) {
                        "superGrocer" -> p0.child("Brand").value.toString()
                        "foodGrocer" -> p0.child("desc").value.toString()
                        else -> "FlyGrocer"
                    }
                    itemName = p0.child("name").value.toString()
                    itemPrice = p0.child("price").value.toString()

                    Picasso.get().load(url).into(ivProduct)
                    tvCompanyName.text = companyName
                    tvItemName.text = itemName
                    tvItemPrice.text = itemPrice
                }
            })

            fab.setOnClickListener {
                if(!presentinBag){
                    Toast.makeText(containerView.context,"Added to Bag", Toast.LENGTH_SHORT).show()
                    bagAddedTime = simpleDateFormat.format(Date())
                    userRef.child("bagItems/$bagType").child(bagAddedTime).child("barcode").setValue(barcode)
                    userRef.child("bagItems/$bagType").child(bagAddedTime).child("qty").setValue(1)
                    fab.setImageResource(R.drawable.ic_baseline_shopping_basket_24)
                    presentinBag = true
                }
                else {
                    Toast.makeText(containerView.context,"Removed from Bag", Toast.LENGTH_SHORT).show()
                    userRef.child("bagItems/$bagType").child(bagAddedTime).removeValue()
                    fab.setImageResource(R.drawable.ic_outline_shopping_basket_24)
                    presentinBag = false
                }
            }

            //creating a popup menu
            val popup = PopupMenu(containerView.context, threeDots)
            popup.inflate(R.menu.delete_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.deleteOption -> {

                        adapter.removeGroupAtAdapterPosition(adapterPosition)
                        if (adapter.itemCount==0)
                            showEmptyContainer.invoke()
                        Toast.makeText(containerView.context,"Removed from Favourites", Toast.LENGTH_SHORT).show()
                        userRef.child("favItems/$addedTime").removeValue()

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

    override fun getLayout(): Int = R.layout.item_fav

}