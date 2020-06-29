package com.titos.flygrocer

import android.widget.ImageView
import android.widget.TextView
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_profile.*

class ProfileItem(private val imageId: Int, val itemName: String): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int){
        viewHolder.apply {
            containerView.findViewById<ImageView>(R.id.profile_item_icon).setImageResource(imageId)
            containerView.findViewById<TextView>(R.id.profile_item_name).text = itemName
        }
    }

    override fun getLayout(): Int = R.layout.item_profile
}