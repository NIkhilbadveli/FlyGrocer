package com.titos.flygrocer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {


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
}