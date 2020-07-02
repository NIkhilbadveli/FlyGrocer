package com.titos.flygrocer

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.json.JSONObject

class CheckoutFragment : Fragment() {
    private var orderTotal = 0
    private var selectedAddress = ""
    private var usingDefault = true
    private lateinit var rvAddresses: RecyclerView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Checkout.preload(requireContext())

        val layoutView =  inflater.inflate(R.layout.fragment_checkout, container, false)

        val paymentButton = layoutView.findViewById<Button>(R.id.paymentButton)
        val tvOrderTotal = layoutView.findViewById<TextView>(R.id.tvOrderTotal)
        val tvDeliveryCharges = layoutView.findViewById<TextView>(R.id.tvDeliveryCharges)
        val tvPayableTotal = layoutView.findViewById<TextView>(R.id.tvPayableTotal)
        val tvAddNewAddress = layoutView.findViewById<TextView>(R.id.tvAddNewAddress)
        rvAddresses = layoutView.findViewById<RecyclerView>(R.id.rvAddresses)

        orderTotal = arguments?.getInt("orderTotal")!!
        val deliveryCharges = 40
        val payableTotal = orderTotal + deliveryCharges

        tvOrderTotal.text = "\u20B9 $orderTotal"
        tvDeliveryCharges.text =  "\u20B9 $deliveryCharges"
        tvPayableTotal.text =  "\u20B9 $payableTotal"

        paymentButton.setOnClickListener {
            if (rvAddresses.childCount>0)
                startPayment(payableTotal*100)
            else
                Toast.makeText(activity,"Please add at least 1 address",Toast.LENGTH_LONG).show()
        }

        tvAddNewAddress.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_editAddressFragment)
        }

        //Recyclerview with addresses
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
            findNavController().navigate(R.id.action_checkoutFragment_to_editAddressFragment, bundle)
        }

        var lastSelectedPosition = 0

        val handleRadioButton = { pos: Int ->
            rvAddresses.getChildAt(lastSelectedPosition).findViewById<RadioButton>(R.id.radioAddress).isChecked = false
            rvAddresses.getChildAt(pos).findViewById<RadioButton>(R.id.radioAddress).isChecked = true
            selectedAddress = getFullAddress(rvAddresses, pos)
            lastSelectedPosition = pos
            usingDefault = false
        }

        userRef.child("addresses").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (id in p0.children){
                    val addressLine1: String = id.child("name").value.toString()
                    val addressLine2: String = id.child("line1").value.toString()
                    val addressLine3: String = id.child("line2").value.toString() + " " + id.child("pincode").value.toString()
                    val mobileNumber: String = id.child("mobileNumber").value.toString()

                    groupAdapter.add(AddressItem(id.key!!.toInt(), addressLine1, addressLine2, addressLine3,
                        mobileNumber, openEditAddress, groupAdapter, true, handleRadioButton))
                }
            }
        })

        return layoutView
    }

    private fun getFullAddress(rvAddresses: RecyclerView, pos: Int): String {
        return rvAddresses.getChildAt(pos).findViewById<TextView>(R.id.tvAddressLine1).text.toString() + ", " +
        rvAddresses.getChildAt(pos).findViewById<TextView>(R.id.tvAddressLine2).text.toString() +
        rvAddresses.getChildAt(pos).findViewById<TextView>(R.id.tvAddressLine3).text.toString() + ", " +
        rvAddresses.getChildAt(pos).findViewById<TextView>(R.id.tvMobileNumber).text.toString()
    }

    private fun startPayment(amount: Int) {
        if (usingDefault)
            selectedAddress = getFullAddress(rvAddresses, 0)

        val activity = activity
        val co = Checkout()
        val phoneNumber = activity?.getSharedPreferences("userData",Context.MODE_PRIVATE)?.getString("phoneNumber","1111122222")

        //Sending order information to MainActivity
        val barcodeList = arguments?.getStringArrayList("barcodeList")!!
        val qtyList = arguments?.getStringArrayList("qtyList")!!

        val model = ViewModelProvider(requireActivity()).get(MainActivity.ViewModelForList::class.java)
        model.sendList(barcodeList,qtyList)
        model.sendOrderTotal(orderTotal)
        model.sendAddress(selectedAddress)

        try {
            val options = JSONObject()
            options.put("name","FlyGrocer")
            options.put("description","Instant Delivery for all your needs")

            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency","INR")
            options.put("amount",amount.toString())

            val prefill = JSONObject()
            prefill.put("email","test@razorpay.com")
            prefill.put("contact","+91$phoneNumber")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}