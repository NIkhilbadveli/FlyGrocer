package com.titos.flygrocer

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.util.*
import kotlin.collections.ArrayList


class ShopFragment : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var onItemClick: (ProductItem)->Unit
    private var productList = ArrayList<ProductItem>()
    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_shop, container, false)

        val dbRef = FirebaseDatabase.getInstance().reference.child("productData")
        val rvProductList = layoutView.findViewById<RecyclerView>(R.id.rvProductList)
        groupAdapter = GroupAdapter<GroupieViewHolder>()

        rvProductList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        onItemClick = {item ->

            val str = ArrayList<String>()

            str.add(item.imageUrl)
            str.add(item.companyName)
            str.add(item.itemName)
            str.add(item.itemPrice)
            str.add(item.barcode)
            str.add(item.presentinBag.toString())

            val bundle = Bundle()
            bundle.putStringArrayList("list",str)

            findNavController().navigate(R.id.action_shopFragment_to_productDetailsFragment, bundle)
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
                    productList.add(ProductItem("00-00-0000 00:00:00",barcode.key!!,url, companyName, itemName, itemPrice, onItemClick, false))
                }
                setData()
            }

        })

        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)!!
        toolbar.findViewById<SearchView>(R.id.searchBar).setOnQueryTextListener(this)
        return layoutView
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val lowerCaseText = newText.toLowerCase(Locale.getDefault())

        if (lowerCaseText.isNotEmpty()) {
            groupAdapter.clear()
            groupAdapter.addAll(productList.filter { it.itemName.toLowerCase(Locale.getDefault()).contains(lowerCaseText) })
        }
        else{
            groupAdapter.clear()
            groupAdapter.addAll(productList)
        }
        return false
    }

    private fun setData(){
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

        userRef.child("bagItems").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(timeStamp in p0.children){
                    productList.first { it.barcode == timeStamp.child("barcode").value.toString() }
                        .presentinBag = true

                    productList.first { it.barcode == timeStamp.child("barcode").value.toString() }
                        .addedTime = timeStamp.key!!

                    productList.first { it.barcode == timeStamp.child("barcode").value.toString() }
                        .itemQuantity = timeStamp.child("qty").value.toString()
                }
                groupAdapter.addAll(productList)
            }
        })
    }
}