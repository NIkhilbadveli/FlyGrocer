package com.titos.flygrocer

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var userRef: DatabaseReference
    private lateinit var orderRef: DatabaseReference
    private lateinit var sharedPref: SharedPreferences
    private val barcodeList = ArrayList<String>()
    private val qtyList = ArrayList<String>()
    private var orderTotal = 0
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNav)
        val user = FirebaseAuth.getInstance().currentUser!!
        userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
        orderRef = FirebaseDatabase.getInstance().reference.child("/allOrders")

        navController.saveState()
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        //Save all userdata to sharedPref
        sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val userName = p0.child("userName").value.toString()
                val cityName = p0.child("cityName").value.toString()
                val mobileNumber = p0.child("phoneNumber").value.toString()
                val referralCode = p0.child("referralCode").value.toString()

                sharedPref.edit().apply {
                    putString("userName", userName)
                    putString("cityName", cityName)
                    putString("phoneNumber", mobileNumber)
                    if (referralCode!="null")
                        putString("referralCode", referralCode)
                    apply()
                }
                if (referralCode=="null")
                    generateAndPutCode()
            }
        })

        val model = ViewModelProvider(this).get(ViewModelForList::class.java)

        model.orderTotal.observe(this) { total ->
            orderTotal = total
        }

        model.barcodeList.observe(this) { list ->
            barcodeList.addAll(list)
        }

        model.qtyList.observe(this) { list ->
            qtyList.addAll(list)
        }

        model.address.observe(this) { str ->
            address = str
        }
    }

    private fun generateAndPutCode() {
        //Generating and saving referral code
        val allowedChars = ('A'..'Z')
        val referralCode = (1..6).map { allowedChars.random() }.joinToString("")
        userRef.child("referralCode").setValue(referralCode)
        FirebaseDatabase.getInstance().reference
            .child("allReferralCodes/$referralCode").setValue(FirebaseAuth.getInstance().currentUser?.uid)
        sharedPref.edit().putString("referralCode", referralCode).apply()
    }

    class ViewModelForList : ViewModel() {
        var barcodeList = MutableLiveData<ArrayList<String>>()
        var qtyList = MutableLiveData<ArrayList<String>>()
        fun sendList(listA: ArrayList<String>, listB: ArrayList<String>){
            barcodeList.value = listA
            qtyList.value = listB
        }

        var orderTotal = MutableLiveData<Int>()
        fun sendOrderTotal(total: Int){orderTotal.value = total}

        var address = MutableLiveData<String>()
        fun sendAddress(string: String){address.value = string}
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Toast.makeText(this,"Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e(ContentValues.TAG,"Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
            val cityName = sharedPref.getString("cityName","DefaultCity")!!.toUpperCase(Locale.ROOT)
            val shortCode = cityName.take(3)
            val totalQty = qtyList.sumBy { it.toInt() }

            orderRef.child(cityName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val orderSuffix = p0.childrenCount.toInt() + 1
                    val orderId = shortCode + "%05d".format(orderSuffix)
                    orderRef.child(cityName).child(orderId).child("orderTotal").setValue(orderTotal)
                    orderRef.child(cityName).child(orderId).child("orderTime").setValue(simpleDateFormat.format(Date()))
                    orderRef.child(cityName).child(orderId).child("razorPayId").setValue(razorpayPaymentId)
                    orderRef.child(cityName).child(orderId).child("orderQty").setValue(totalQty)
                    orderRef.child(cityName).child(orderId).child("orderStatus").setValue("Pending")
                    orderRef.child(cityName).child(orderId).child("deliveryAddress").setValue(address)
                    orderRef.child(cityName).child(orderId).child("userId").setValue(userRef.key)

                    for (i in 0 until barcodeList.size){
                        val key = userRef.push().key!!
                        orderRef.child(cityName).child(orderId).child("orderedItems/${key}/barcode")
                            .setValue(barcodeList[i])
                        orderRef.child(cityName).child(orderId).child("orderedItems/${key}/qty")
                            .setValue(qtyList[i])
                    }

                    //Save only the order number into userdata
                    userRef.child("allOrders").child(userRef.push().key!!).setValue(orderId)

                    //Clearing bag
                    userRef.child("bagItems").removeValue()

                    val bundle = Bundle()
                    bundle.putString("orderId", orderId)
                    findNavController(R.id.nav_host_fragment_container).navigate(R.id.action_checkoutFragment_to_successFragment, bundle)
                }
            })
        }catch (e: Exception){
            Toast.makeText(this,"Payment failed: $e", Toast.LENGTH_LONG).show()
            Log.e(ContentValues.TAG,"Exception in onPaymentSuccess", e)
        }
    }
}