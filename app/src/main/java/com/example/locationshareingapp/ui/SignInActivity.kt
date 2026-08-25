package com.example.locationshareingapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.locationshareingapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sign In বাটনে ক্লিক করলে লগইন প্রোসেস শুরু
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authViewModel.signIn(email, pass)
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Sign Up পেজে যাওয়ার লিংক
        binding.tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Auth Result অবজার্ভ করে FriendListActivity-তে পাঠাবে
        authViewModel.authResult.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, FriendListActivity::class.java))
                finish()
            }
        }

        authViewModel.errorMessage.observe(this) { err ->
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
        }
    }
}