package com.titos.flygrocer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_home, container, false)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1))
        imageList.add(SlideModel(R.drawable.banner2))
        imageList.add(SlideModel(R.drawable.banner3))
        imageList.add(SlideModel(R.drawable.banner4))

        val imageSlider = layoutView.findViewById<ImageSlider>(R.id.bannerSlider)
        imageSlider.setImageList(imageList)

        val products = layoutView.findViewById<CardView>(R.id.products)
        val prodRef = FirebaseDatabase.getInstance().reference.child("productData/foodGrocer")

        products.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_shopFragment)
        }

        prodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                val image1 = layoutView.findViewById<ImageView>(R.id.img1)
                val tvName = layoutView.findViewById<TextView>(R.id.tv_name1)
                val tvPrice = layoutView.findViewById<TextView>(R.id.tv_price1)
                val imgUrl = p0.child("3001/url").value.toString()
                Picasso.get().load(imgUrl).into(image1)
                tvName.text= p0.child("3001/name").value.toString()
                tvPrice.text= p0.child("3001/price").value.toString()

                val image2 = layoutView.findViewById<ImageView>(R.id.img2)
                val tvName2 = layoutView.findViewById<TextView>(R.id.tv_name2)
                val tvPrice2 = layoutView.findViewById<TextView>(R.id.tv_price2)
                val imgUrl2 = p0.child("3002/url").value.toString()
                Picasso.get().load(imgUrl2).into(image2)
                tvName2.text= p0.child("3002/name").value.toString()
                tvPrice2.text= p0.child("3002/price").value.toString()

                val image3 = layoutView.findViewById<ImageView>(R.id.img3)
                val tvName3 = layoutView.findViewById<TextView>(R.id.tv_name3)
                val tvPrice3 = layoutView.findViewById<TextView>(R.id.tv_price3)
                val imgUrl3 = p0.child("3003/url").value.toString()
                Picasso.get().load(imgUrl3).into(image3)
                tvName3.text= p0.child("3003/name").value.toString()
                tvPrice3.text= p0.child("3003/price").value.toString()

                val image4 = layoutView.findViewById<ImageView>(R.id.img4)
                val tvName4 = layoutView.findViewById<TextView>(R.id.tv_name4)
                val tvPrice4 = layoutView.findViewById<TextView>(R.id.tv_price4)
                val imgUrl4 = p0.child("3004/url").value.toString()
                Picasso.get().load(imgUrl4).into(image4)
                tvName4.text= p0.child("3004/name").value.toString()
                tvPrice4.text= p0.child("3004/price").value.toString()
            }
        })

        return layoutView
    }

}