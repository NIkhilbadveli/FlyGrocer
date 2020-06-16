package com.titos.flygrocer

import android.widget.ImageView

import android.widget.TextView

import com.squareup.picasso.Picasso

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item


class ProductItem( val imageUrl: String, val companyName: String, val itemName: String, val itemPrice: String,
val onItemClick: ((ProductItem)-> Unit)): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {

            val ivProduct = containerView.findViewById<ImageView>(R.id.ivProduct)
            containerView.findViewById<TextView>(R.id.tvItemName).text = itemName
            containerView.findViewById<TextView>(R.id.tvCompanyName).text = companyName
            containerView.findViewById<TextView>(R.id.tvItemPrice).text = itemPrice

            ivProduct.setOnClickListener { onItemClick.invoke(this@ProductItem) }
            Picasso.get().load(imageUrl).into(ivProduct)

        }
    }

    override fun getLayout(): Int = R.layout.item_product
}