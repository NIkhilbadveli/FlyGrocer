package com.titos.flygrocer

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item


class ProductItem(private val imageId: Int, val companyName: String, val itemName: String, val itemPrice: String): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {

            val relativeLayout = containerView.findViewById<RelativeLayout>(R.id.imageForProduct)
            containerView.findViewById<TextView>(R.id.tvItemName).text = itemName
            containerView.findViewById<TextView>(R.id.tvCompanyName).text = companyName
            containerView.findViewById<TextView>(R.id.tvItemPrice).text = itemPrice

            relativeLayout.setBackgroundResource(imageId)
        }
    }

    override fun getLayout(): Int = R.layout.item_product
}