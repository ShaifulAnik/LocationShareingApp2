package com.example.locationshareingapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.locationshareingapp.databinding.ActivityMyProfileBinding
import com.google.firebase.auth.FirebaseAuth

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userViewModel.fetchAllUsers()
        userViewModel.users.observe(this) { users ->
            val currentUser = users.find { it.userId == currentUid }
            currentUser?.let {
                binding.etProfileName.setText(it.displayName ?: "")
                binding.tvProfileEmail.text = "Email: ${it.userEmail}"
                binding.tvProfileLat.text = "Latitude: ${it.latitude}"
                binding.tvProfileLon.text = "Longitude: ${it.longitude}"
            }
        }

        // Name আপডেট করার বাটন ক্লিক
        binding.btnUpdateProfile.setOnClickListener {
            val newName = binding.etProfileName.text.toString().trim()
            if (newName.isNotEmpty()) {
                userViewModel.updateDisplayName(currentUid, newName) {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}