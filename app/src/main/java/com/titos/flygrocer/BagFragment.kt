package com.titos.flygrocer

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
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

class BagFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layoutView =  inflater.inflate(R.layout.fragment_bag, container, false)

        val tvTotal = layoutView.findViewById<TextView>(R.id.totalAmount)
        val rvProductList = layoutView.findViewById<RecyclerView>(R.id.rvBagList)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

        rvProductList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        val checkoutButton = layoutView.findViewById<Button>(R.id.checkoutButton)
        val bundle = Bundle()
        checkoutButton.setOnClickListener{
            bundle.putInt("orderTotal", tvTotal.text.toString().split(" ").last().toInt())
            if (tvTotal.text.toString().split(" ").last().toInt()>0)
                findNavController().navigate(R.id.action_bagFragment_to_checkoutFragment, bundle)
            else
                Toast.makeText(activity,"Please add at least 1 item", Toast.LENGTH_LONG).show()
        }

        val barcodeList = ArrayList<String>()
        val qtyList = ArrayList<String>()
        userRef.child("bagItems").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(timeStamp in p0.children){
                        val barcode = timeStamp.child("barcode").value.toString()
                        val qty = timeStamp.child("qty").value.toString()

                        groupAdapter.add(BagItem(timeStamp.key!!,barcode,qty,tvTotal, groupAdapter))
                        barcodeList.add(barcode)
                        qtyList.add(qty)
                    }
                    bundle.putStringArrayList("barcodeList", barcodeList)
                    bundle.putStringArrayList("qtyList", qtyList)
                }
            }
        })

        return layoutView
    }
}