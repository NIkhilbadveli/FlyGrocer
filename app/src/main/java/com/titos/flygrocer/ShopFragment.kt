package com.titos.flygrocer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ShopFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_shop, container, false)

        val dbRef = FirebaseDatabase.getInstance().reference.child("productData")
        val rvProductList = layoutView.findViewById<RecyclerView>(R.id.rvProductList)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        val productList = ArrayList<ProductItem>()
        val imageLoader = ImageLoader.getInstance()
        imageLoader.init(ImageLoaderConfiguration.createDefault(activity))
        rvProductList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = groupAdapter
        }

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(barcode in p0.children){
                    val url = barcode.child("URL").value.toString()
                    val companyName = barcode.child("Brand").value.toString()
                    val itemName = barcode.child("name").value.toString()
                    val itemPrice = barcode.child("price").value.toString()
                    groupAdapter.add(ProductItem(R.drawable.grocery, companyName, itemName, itemPrice))
                }
            }

        })

        return layoutView
    }


}