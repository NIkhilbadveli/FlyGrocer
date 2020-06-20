package com.titos.flygrocer

import android.content.ContentValues
import android.content.Intent
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), PaymentResultListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNav)

        navController.saveState()
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
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
        //Move to sharedPref later
        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")
        val orderRef = FirebaseDatabase.getInstance().reference.child("/allOrders")
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val cityName = p0.child("cityName").value.toString().toUpperCase(Locale.ROOT)
                //Get max order id and add 1 to it
                val orderId = "${cityName}_0001"
                orderRef.child(simpleDateFormat.format(Date())).child(orderId).child("orderAmount").setValue(100)
                orderRef.child(simpleDateFormat.format(Date())).child(orderId).child("razorPayId").setValue(razorpayPaymentId)
                //Clear bag after this
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