package com.titos.flygrocer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class AddressFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_address, container, false)
        val rvAddresses = layoutView.findViewById<RecyclerView>(R.id.rvAddresses)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

        rvAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        val openEditAddress = { addressId: Int ->
            val bundle = Bundle()
            bundle.putInt("addressId", addressId)
            findNavController().navigate(R.id.action_addressFragment_to_editAddressFragment, bundle)
        }

        var lastSelectedPosition = 0
        val handleRadioButton = { pos: Int ->
            rvAddresses.getChildAt(lastSelectedPosition).findViewById<RadioButton>(R.id.radioAddress).isChecked = false
            rvAddresses.getChildAt(pos).findViewById<RadioButton>(R.id.radioAddress).isChecked = true
            lastSelectedPosition = pos
        }

        val showEmptyContainer = {
            layoutView.findViewById<LinearLayout>(R.id.emptyContainer).visibility = View.VISIBLE
        }

        userRef.child("addresses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.hasChildren())
                    layoutView.findViewById<LinearLayout>(R.id.emptyContainer).visibility = View.VISIBLE

                for (id in p0.children){
                    val addressLine1: String = id.child("name").value.toString()
                    val addressLine2: String = id.child("line1").value.toString()
                    val addressLine3: String = id.child("line2").value.toString() + " " + id.child("pincode").value.toString()
                    val mobileNumber: String = id.child("mobileNumber").value.toString()

                    groupAdapter.add(AddressItem(id.key!!.toInt(), addressLine1, addressLine2, addressLine3,
                        mobileNumber, openEditAddress, groupAdapter, false, handleRadioButton, showEmptyContainer))
                }
            }
        })

        layoutView.findViewById<Button>(R.id.addNewAddressButton).setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_editAddressFragment)
        }

        return layoutView
    }
}