package com.titos.flygrocer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


class ShopFragment : Fragment() {
    private lateinit var onItemClick: (ProductItem)->Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_shop, container, false)

        val dbRef = FirebaseDatabase.getInstance().reference.child("productData")
        val rvProductList = layoutView.findViewById<RecyclerView>(R.id.rvProductList)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        val productList = ArrayList<ProductItem>()

        val viewModel: MainActivity.ViewModelForList by viewModels()

        rvProductList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = groupAdapter
        }

        onItemClick = {item ->

            val str = ArrayList<String>()
            str.add(item.imageUrl)
            str.add(item.companyName)
            str.add(item.itemName)
            str.add(item.itemPrice)

            viewModel.sendList(str)

            findNavController().navigate(R.id.action_shopFragment_to_productDetailsFragment)
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
                    productList.add(ProductItem(url, companyName, itemName, itemPrice, onItemClick))
                }
                groupAdapter.addAll(productList)
            }

        })

        return layoutView
    }
}