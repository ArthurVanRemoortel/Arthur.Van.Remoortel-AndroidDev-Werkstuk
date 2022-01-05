package com.example.arthurvanremoortel_werkstuk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModelProvider
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        binding = ActivityMainBinding.inflate(layoutInflater)
        val myRoot = binding.root
        setContentView(myRoot)


        binding.button.setOnClickListener {
            binding.textView.text = "clicked!"

        }

        if (auth.currentUser == null) {
            Log.d("LOGIN", "User is null")
        } else {
            Log.d("LOGIN", "User is OK")
        }
    }
}