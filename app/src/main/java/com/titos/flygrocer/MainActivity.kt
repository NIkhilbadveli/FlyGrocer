package com.titos.flygrocer

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var userRef: DatabaseReference
    private lateinit var orderRef: DatabaseReference
    private lateinit var sharedPref: SharedPreferences

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
                val cityName = p0.child("cityName").value.toString()
                val mobileNumber = p0.child("mobileNumber").value.toString()
                sharedPref.edit().apply {
                    putString("cityName", cityName)
                    putString("mobileNumber", mobileNumber)
                    apply()
                }
            }
        })


    }

    class ViewModelForList : ViewModel() {
        val finalList = MutableLiveData<ArrayList<String>>()

        fun sendList(list: ArrayList<String>){
            finalList.value = list
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Toast.makeText(this,"Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e(ContentValues.TAG,"Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val cityName = sharedPref.getString("cityName","DefaultCity")!!.toUpperCase(Locale.ROOT)
        val shortCode = cityName.take(3)

        orderRef.child(cityName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                //var orderSuffix = 1
                //if (p0.exists())
                    val orderSuffix = p0.childrenCount.toInt() + 1

                val orderId = shortCode + "%05d".format(orderSuffix)
                orderRef.child(cityName).child(orderId).child("orderAmount").setValue(100)
                orderRef.child(cityName).child(orderId).child("orderTime").setValue(simpleDateFormat.format(Date()))
                orderRef.child(cityName).child(orderId).child("razorPayId").setValue(razorpayPaymentId)
                orderRef.child(cityName).child(orderId).child("orderQty").setValue(1)

                //Save only the order number into userdata
                userRef.child("allOrders").child(userRef.push().key!!).setValue(orderId)

                //Clearing bag
                userRef.child("bagItems").removeValue()

                val bundle = Bundle()
                bundle.putString("orderId", orderId)
                findNavController(R.id.nav_host_fragment_container).navigate(R.id.action_checkoutFragment_to_successFragment, bundle)
            }
        })

        try{

        }catch (e: Exception){
            Log.e(ContentValues.TAG,"Exception in onPaymentSuccess", e)
        }
    }
}