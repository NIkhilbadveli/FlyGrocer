package com.titos.flygrocer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshot.Index
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val layoutView =  inflater.inflate(R.layout.fragment_favourites, container, false)

        val rvFav = layoutView.findViewById<RecyclerView>(R.id.rvFavourites)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        val productList = ArrayList<FavItem>()
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
         rvFav.apply {
             layoutManager = LinearLayoutManager(context)
             adapter = groupAdapter
         }

        userRef.child("My Favs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(barcode in p0.children){
                     productList.add(FavItem(barcode.value.toString()))
                }
                groupAdapter.addAll(productList)
            }

        })

        return layoutView
    }
}