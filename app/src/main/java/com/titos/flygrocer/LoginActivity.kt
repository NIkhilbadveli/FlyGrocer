package com.titos.flygrocer

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class LoginActivity : AppCompatActivity() {
    companion object {
        init {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }

    private val TAG = "FlyGrocerTag"
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var otpSent = false
    private var otpResent = false
    private lateinit var databaseRef: DatabaseReference
    private var liveCities = ArrayList<String>()
    private lateinit var name: String
    private lateinit var phoneNum: String
    private var referralCode = "NBR000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val sendOtpButton = findViewById<Button>(R.id.buttonLogin)
        val inputPhone = findViewById<EditText>(R.id.editTextPhone)

        val otpView = findViewById<OtpTextView>(R.id.inputOTP)
        val tvCountDownTimer = findViewById<TextView>(R.id.tvCountDown)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        val countDownTimer = object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvCountDownTimer.text = "${(millisUntilFinished / 1000)}s"
            }
            override fun onFinish() {
                tvCountDownTimer.paintFlags = tvCountDownTimer.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                tvCountDownTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                tvCountDownTimer.text = "Resend OTP"
            }
        }

        if(auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        databaseRef.child("liveCities").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children){
                    liveCities.add(child.value.toString())
                }
            }
        })

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            checkReferralCode()
                            checkAvailability()
                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(this@LoginActivity, e.toString(),Toast.LENGTH_LONG).show()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.w(TAG, "InvalidCredentials", e)
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.w(TAG, "TooManySMS", e)
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                tvCountDownTimer.visibility = View.VISIBLE
                tvCountDownTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                countDownTimer.start()
                Log.d(TAG, "onCodeSent:$verificationId")

                storedVerificationId = verificationId
                resendToken = token
            }
        }
        
        sendOtpButton.setOnClickListener {
            otpView.requestFocusOTP()
            if (inputPhone.text.isNotEmpty() && !otpSent){
                var phoneNumber = inputPhone.text.toString()
                if (phoneNumber.length==10)
                    phoneNumber = "+91$phoneNumber"

                if (phoneNumber.take(3) == "+91" && phoneNumber.length!=13)
                    Toast.makeText(this,"Please check your phone number", Toast.LENGTH_SHORT).show()
                else {
                    phoneNum = phoneNumber
                    //Perform check on given phone number
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this, // Activity (for callback binding)
                        callbacks) // OnVerificationStateChangedCallbacks

                        findViewById<LinearLayout>(R.id.otpContainer).visibility = View.VISIBLE
                        sendOtpButton.text = "Login"
                        otpSent = true
                }
            }
            else if (otpSent && otpView.otp.length==6)
                verifyVerificationCode(otpView.otp)
            else if (otpSent && otpView.otp.isEmpty())
                Toast.makeText(this,"Please wait for OTP", Toast.LENGTH_SHORT).show()
            else if(inputPhone.text.isEmpty())
                Toast.makeText(this,"Please Enter your phone number", Toast.LENGTH_SHORT).show()

        }

        tvCountDownTimer.setOnClickListener {
            if (!otpResent){
                var phoneNumber = inputPhone.text.toString()
                if (phoneNumber.length==10)
                    phoneNumber = "+91$phoneNumber"

                if (phoneNumber.take(3) == "+91" && phoneNumber.length!=13)
                    Toast.makeText(this,"Please check your phone number", Toast.LENGTH_SHORT).show()
                else {
                    phoneNum = phoneNumber
                    //Perform check on given phone number
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this, // Activity (for callback binding)
                        callbacks) // OnVerificationStateChangedCallbacks
                    Toast.makeText(this,"OTP resent", Toast.LENGTH_SHORT).show()
                    otpResent = true
                }
            }
        }
    }

    private fun verifyVerificationCode(otp: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        val pd = ProgressDialog.progressDialog(this)
        pd.show()
        //Signing in after verification
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    pd.dismiss()
                    checkReferralCode()
                    checkAvailability()
                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun checkAvailability(){
        Toast.makeText(this,"Successfully signed-in", Toast.LENGTH_SHORT).show()
        val user = auth.currentUser!!

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.availability_check)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)

        val nextButton = dialog.findViewById<Button>(R.id.nextButton)
        val spinnerLiveCities = dialog.findViewById<Spinner>(R.id.spinnerLiveCities)
        val inputName = dialog.findViewById<EditText>(R.id.editTextName)

        spinnerLiveCities.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, liveCities)

        val pd = ProgressDialog.progressDialog(this)
        pd.findViewById<TextView>(R.id.login_tv_dialog).text = "Checking for availability..."
        pd.show()
        databaseRef.child("userData").child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    pd.dismiss()
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }
                else{
                    //Saving user data
                    databaseRef.child("userData").child(user.uid).child("phoneNumber").setValue(phoneNum)
                    pd.dismiss()
                    dialog.show()
                }
            }

        })


        nextButton.setOnClickListener {
            if (spinnerLiveCities.selectedItem!=null && inputName.text.isNotEmpty()){
                databaseRef.child("userData").child(user.uid).child("userName").setValue(inputName.text.toString())
                databaseRef.child("userData").child(user.uid).child("cityName").setValue(spinnerLiveCities.selectedItem.toString())
                dialog.dismiss()
                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                finish()
            }
            else if(inputName.text.isEmpty())
                Toast.makeText(this,"Please Enter your full name", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,"Please select one of the above listed cities", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkReferralCode(){
        val etReferralCode = findViewById<EditText>(R.id.etReferralCode)
        if (etReferralCode.text.length==6)
            referralCode = etReferralCode.text.toString()
        else if (etReferralCode.text.isNotEmpty())
            Toast.makeText(this,"Check the code once again", Toast.LENGTH_SHORT).show()

        referralCode = referralCode.toUpperCase(Locale.ROOT)
        var referrerUid: String
        val pd = ProgressDialog.progressDialog(this)
        pd.findViewById<TextView>(R.id.login_tv_dialog).text = "Validating referral code..."
        pd.show()
        databaseRef.child("allReferralCodes/$referralCode").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        pd.dismiss()
                        referrerUid = p0.value.toString()
                        databaseRef.child("userData/${auth.currentUser!!.uid}/rewardPoints").setValue(500)
                        databaseRef.child("userData/${referrerUid}/rewardPoints").setValue(500)
                        Toast.makeText(this@LoginActivity,"Yayy! You earned 500 points!", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        pd.dismiss()
                        Toast.makeText(this@LoginActivity,"This code doesn't belong to anyone", Toast.LENGTH_SHORT).show()
                    }
                }
        })
    }
}