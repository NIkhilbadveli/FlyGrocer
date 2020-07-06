package com.titos.flygrocer

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
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
    private lateinit var onItemClick: (FavItem)->Unit
    private var productList = ArrayList<FavItem>()
    private var groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var pd : Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val layoutView =  inflater.inflate(R.layout.fragment_favourites, container, false)

        val rvFav = layoutView.findViewById<RecyclerView>(R.id.rvFavourites)
        pd = ProgressDialog.progressDialog(requireContext())
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

        onItemClick = {item ->
            val str = ArrayList<String>()
            str.add(item.url)
            str.add(item.companyName)
            str.add(item.itemName)
            str.add(item.itemPrice)
            str.add(item.barcode)
            str.add(item.presentinBag.toString())

            val bundle = Bundle()
            bundle.putStringArrayList("list",str)

            findNavController().navigate(R.id.action_favouritesFragment_to_productDetailsFragment, bundle)
        }

        rvFav.apply {
             layoutManager = GridLayoutManager(requireContext(),2)
             adapter = groupAdapter
        }

        pd.findViewById<TextView>(R.id.login_tv_dialog).text = "Please wait..."
        pd.setCanceledOnTouchOutside(false)
        pd.show()
        userRef.child("favItems").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()){
                    for(timeStamp in p0.children){
                         productList.add(FavItem(timeStamp.value.toString(),false, "00-00-0000 00:00:00", onItemClick))
                    }
                    setData()
                }
                else
                    layoutView.findViewById<LinearLayout>(R.id.emptyContainer).visibility = View.VISIBLE
            }
        })

        return layoutView
    }

    private fun setData(){
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

        userRef.child("bagItems").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for(timeStamp in p0.children){
                    productList.firstOrNull { it.barcode == timeStamp.child("barcode").value.toString() }?.addedTime = timeStamp.key!!
                    productList.firstOrNull { it.barcode == timeStamp.child("barcode").value.toString() }?.presentinBag = true
                }
                groupAdapter.addAll(productList)
                pd.dismiss()
            }
        })
    }
}