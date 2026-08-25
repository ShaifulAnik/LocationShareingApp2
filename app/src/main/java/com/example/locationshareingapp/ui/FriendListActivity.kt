package com.example.locationshareingapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationshareingapp.databinding.ActivityFriendListBinding
import com.example.locationshareingapp.R
import com.example.locationshareingapp.adapter.UserAdapter
import com.example.locationshareingapp.data.AppUser
import com.google.firebase.auth.FirebaseAuth

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Drawer Toggle setup
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.app_name, R.string.app_name
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // RecyclerView Adapter initialization
        adapter = UserAdapter(emptyList()) { selectedUser ->
            // ইউজার আইটেমে ক্লিক করলে শুধু ঐ യൂজারকে দেখানোর জন্য Extra ID পাঠানো হচ্ছে
            val intent = Intent(this, GoogleMapActivity::class.java).apply {
                putExtra("SINGLE_USER_ID", selectedUser.userId)
            }
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Firestore থেকে সকল యూজার তালিকা আনা
        userViewModel.fetchAllUsers()
        userViewModel.users.observe(this) { list ->
            adapter.updateData(list)
            updateDrawerHeader(list)
        }

        // Show All Users on Map Button listener
        binding.btnShowAllUsersMap.setOnClickListener {
            startActivity(Intent(this, GoogleMapActivity::class.java))
        }

        // Drawer Option Selection Handling
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> startActivity(Intent(this, MyProfileActivity::class.java))
                R.id.nav_map -> startActivity(Intent(this, GoogleMapActivity::class.java))
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finishAffinity()
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    // Drawer Header-এ বর্তমান লগিন হওয়া ইউজারের নাম, ইমেইল এবং লোকেশন আপডেট করার ফাংশন
    private fun updateDrawerHeader(users: List<AppUser>) {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        val currentUser = users.find { it.userId == currentUid }

        val headerView = binding.navigationView.getHeaderView(0)
        val tvName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvEmail = headerView.findViewById<TextView>(R.id.tvHeaderEmail)
        val tvLoc = headerView.findViewById<TextView>(R.id.tvHeaderLocation)

        currentUser?.let {
            tvName.text = it.displayName ?: "Logged-in User"
            tvEmail.text = it.userEmail
            tvLoc.text = "Lat: ${it.latitude}, Lon: ${it.longitude}"
        }
    }
}