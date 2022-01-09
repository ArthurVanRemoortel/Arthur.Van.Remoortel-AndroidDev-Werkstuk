package com.example.arthurvanremoortel_werkstuk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.arthurvanremoortel_werkstuk.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val myRoot = binding.root
        setContentView(myRoot)

        auth = Firebase.auth

        binding.registerButton.setOnClickListener {
            this.handleRegister()
        }

        binding.registerText.setOnClickListener {
            finish()
        }
    }


    fun handleRegister() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        val confirmPassword = binding.passwordConfirmInput.text.toString().trim()

        if (TextUtils.isEmpty(email)){
            binding.emailInput.setError("Email is required.")
            return
        }
        if (TextUtils.isEmpty(password)){
            binding.passwordInput.setError("Password is required.")
            return
        }
        if (password.length < 6){
            binding.passwordConfirmInput.setError("Password must be longer than 6 characters")
            return
        }
        if (TextUtils.isEmpty(confirmPassword)){
            binding.passwordConfirmInput.setError("Confirm password is required.")
            return
        }
        if (password != confirmPassword){
            binding.passwordConfirmInput.setError("Passwords are not the same")
            return
        }
        binding.activityProgressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LOGIN", "createUserWithEmail:success")
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LOGIN", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: Show reason for fail.
                }
                binding.activityProgressBar.visibility = View.INVISIBLE
            }
    }
}