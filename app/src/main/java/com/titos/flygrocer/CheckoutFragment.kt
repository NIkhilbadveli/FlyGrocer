package com.titos.flygrocer

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class CheckoutFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Checkout.preload(requireContext())

        val layoutView =  inflater.inflate(R.layout.fragment_checkout, container, false)

        val paymentButton = layoutView.findViewById<Button>(R.id.paymentButton)
        val tvOrderTotal = layoutView.findViewById<TextView>(R.id.tvOrderTotal)
        val tvDeliveryCharges = layoutView.findViewById<TextView>(R.id.tvDeliveryCharges)
        val tvPayableTotal = layoutView.findViewById<TextView>(R.id.tvPayableTotal)

        val orderTotal = arguments?.getInt("orderTotal")!!
        val deliveryCharges = 40
        val payableTotal = orderTotal + deliveryCharges

        tvOrderTotal.text = "\u20B9 $orderTotal"
        tvDeliveryCharges.text =  "\u20B9 $deliveryCharges"
        tvPayableTotal.text =  "\u20B9 $payableTotal"

        paymentButton.setOnClickListener {
            //findNavController().navigate(R.id.action_checkoutFragment_to_trackingFragment)
            startPayment(payableTotal*100)
        }

        return layoutView
    }

    private fun startPayment(amount: Int) {

        val activity = activity
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","FlyGrocer")
            options.put("description","Instant Delivery for all your needs")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency","INR")
            options.put("amount",amount.toString())

            val prefill = JSONObject()
            prefill.put("email","test@razorpay.com")
            prefill.put("contact","9876543210")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


}