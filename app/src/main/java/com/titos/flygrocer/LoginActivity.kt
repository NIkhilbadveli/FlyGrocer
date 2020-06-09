package com.titos.flygrocer

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private val TAG = "FlyGrocerTag"
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var otpSent = false
    private var otpResent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sendOtpButton = findViewById<Button>(R.id.buttonLogin)
        val inputPhone = findViewById<EditText>(R.id.editTextPhone)
        val otpView = findViewById<OtpTextView>(R.id.inputOTP)
        val tvCountDownTimer = findViewById<TextView>(R.id.tvCountDown)
        auth = FirebaseAuth.getInstance()

        val countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCountDownTimer.text = "${(millisUntilFinished / 1000)}s"
            }
            override fun onFinish() {
                tvCountDownTimer.paintFlags = tvCountDownTimer.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                tvCountDownTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                tvCountDownTimer.text = "Resend OTP"

                Log.d("tekloon", "onFinish")
            }
        }

        if(auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                /*val code: String = credential.smsCode!!

                otpView.otp = code
                //verifying the code
                verifyVerificationCode(code)*/
                Toast.makeText(this@LoginActivity,"Successfully signed-in", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.w(TAG, "onVerificationFailed", e)

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
            if (inputPhone.text.isNotEmpty() && !otpSent){
                val phoneNumber = inputPhone.text.toString()
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91$phoneNumber", // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    callbacks) // OnVerificationStateChangedCallbacks

                    findViewById<LinearLayout>(R.id.otpContainer).visibility = View.VISIBLE
                    sendOtpButton.text = "Login"
                    otpSent = true
            }
            else if (otpSent && otpView.otp.length==6)
                verifyVerificationCode(otpView.otp)
            else if (otpSent && otpView.otp.isEmpty())
                Toast.makeText(this,"Please wait for OTP", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,"Please Enter your phone number", Toast.LENGTH_SHORT).show()
        }

        tvCountDownTimer.setOnClickListener {
            if (!otpResent){
                val phoneNumber = inputPhone.text.toString()
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91$phoneNumber", // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    callbacks) // OnVerificationStateChangedCallbacks

                otpResent = true
            }
        }
    }

    private fun verifyVerificationCode(otp: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)

        //Signing in after verification
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Successfully signed-in", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                    val user = task.result?.user

                } else {

                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }
}