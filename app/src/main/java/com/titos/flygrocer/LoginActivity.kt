package com.titos.flygrocer


import android.app.Activity

import android.content.Intent

import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {
    companion object {
        init {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }
    }
    private val RC_SIGN_IN = 123
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        val authMethodPickerLayout = AuthMethodPickerLayout
            .Builder(R.layout.activity_login)
            .setGoogleButtonId(R.id.google_signin_button)
            .setEmailButtonId(R.id.email_signin_button)
            .setPhoneButtonId(R.id.otp_signin_button)
            .build()

        if(auth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAuthMethodPickerLayout(authMethodPickerLayout)
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                    .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build(),
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("in").setWhitelistedCountries(listOf("+91")).build()))
                    .setTheme(R.style.AppThemeFirebaseAuth)
                    .build(),
                RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){

            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this,"Sign In successful",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()

                return
            }
            else {
                if(response == null){
                    //If no response from the Server
                    return
                }
                if(response.error!!.errorCode == ErrorCodes.NO_NETWORK){
                    //If there was a network problem the user's phone

                    return
                }
                if(response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR){
                    //If the error cause was unknown

                    return
                }
            }
        }
    }
}