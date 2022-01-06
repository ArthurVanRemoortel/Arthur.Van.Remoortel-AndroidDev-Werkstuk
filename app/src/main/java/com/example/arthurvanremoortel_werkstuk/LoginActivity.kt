package com.example.arthurvanremoortel_werkstuk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val myRoot = binding.root
        setContentView(myRoot)
        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            this.handleLogin()

        }

        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java)) // TODO: Maybe dismiss current activity instead of adding new on.
        }
    }


    fun handleLogin(){
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        if (TextUtils.isEmpty(email)){
            binding.emailInput.setError("Email is required.")
            return
        }
        if (TextUtils.isEmpty(password)){
            binding.passwordInput.setError("Password is required.")
            return
        }
        binding.activityProgressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LOGIN", "signInWithEmail:success")
                    //val user = auth.currentUser

                    startActivity(Intent(this, MainActivity::class.java))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    // TODO: Show reason for fail.
                    //binding.activityProgressBar.visibility = View.INVISIBLE

                }
                binding.activityProgressBar.visibility = View.INVISIBLE
            }
    }

}