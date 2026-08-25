package com.example.locationshareingapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.locationshareingapp.databinding.ActivitySignUpBinding
import com.example.locationshareingapp.data.AppUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Sign Up বাটনে ক্লিক
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmailSignUp.text.toString().trim()
            val pass = binding.etPasswordSignUp.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authViewModel.signUp(email, pass)
            } else {
                Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToSignIn.setOnClickListener {
            finish() // Sign In পেজে ফিরে যাবে
        }

        authViewModel.authResult.observe(this) { success ->
            if (success) {
                // অ্যাকাউন্ট রেজিস্টার শেষে বর্তমান লোকেশন বের করে Firestore-এ ডাটা ফেস করবে
                fetchLocationAndSaveUser()
            }
        }

        authViewModel.errorMessage.observe(this) { err ->
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show()
        }
    }

    // Fused Location Provider Client দিয়ে Latitude & Longitude রিট্রিভ করার ফাংশন
    private fun fetchLocationAndSaveUser() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0
            val currentUser = FirebaseAuth.getInstance().currentUser

            currentUser?.let {
                val appUser = AppUser(
                    userId = it.uid,
                    userEmail = it.email ?: "",
                    displayName = null, // ডিফল্টভাবে null থাকছে
                    latitude = lat,
                    longitude = lon
                )
                // Firestore-এ সেভ করে সরাসরি FriendListActivity-তে চলে যাবে
                userViewModel.saveUserToFirestore(appUser) {
                    startActivity(Intent(this, FriendListActivity::class.java))
                    finishAffinity()
                }
            }
        }
    }
}