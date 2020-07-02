package com.titos.flygrocer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RewardsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutView = inflater.inflate(R.layout.fragment_rewards, container, false)

        val user = FirebaseAuth.getInstance().currentUser!!
        val userRef = FirebaseDatabase.getInstance().reference.child("/userData/${user.uid}")

        val tvReferralCode = layoutView.findViewById<TextView>(R.id.tvReferralCode)
        val tvPointsRupees = layoutView.findViewById<TextView>(R.id.tvPointsRupees)

        userRef.child("rewardPoints").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val points = p0.value.toString().toInt()
                    val rupees = points/5
                    tvPointsRupees.text = "$points Points = ₹ $rupees"
                }
            }
        })

        val sharedPref = activity?.getSharedPreferences("userData", Context.MODE_PRIVATE)!!
        val referralCode = sharedPref.getString("referralCode","NBR000")!!
        /*if (referralCode=="NBR000") {
            val allowedChars = ('A'..'Z')
            referralCode = (1..6).map { allowedChars.random() }.joinToString("")
            userRef.child("referralCode").setValue(referralCode)
            sharedPref.edit().putString("referralCode", referralCode).apply()
        }*/

        tvReferralCode.text = referralCode
        tvReferralCode.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("referralCodeFlyGrocer",referralCode))
            Toast.makeText(requireContext(),"Code copied!", Toast.LENGTH_LONG).show()
        }

        val msg = "Invite Your friends on Flygrocer and after they join, both of you " +
                "will get 500 points which can used as coupons in your cart \n \n Here's the referral code: " + referralCode
        layoutView.findViewById<ImageView>(R.id.fbIcon).setOnClickListener {  }
        layoutView.findViewById<ImageView>(R.id.whatsAppIcon).setOnClickListener {  }
        layoutView.findViewById<ImageView>(R.id.shareIcon).setOnClickListener {
            ShareCompat.IntentBuilder.from(requireActivity())
                .setType("text/plain")
                .setChooserTitle("Send the referral code via...")
                .setText(msg)
                .startChooser()
        }

        return layoutView
    }
}