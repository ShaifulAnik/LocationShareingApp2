package com.example.locationshareingapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.locationshareingapp.databinding.ActivityGoogleMapBinding
import com.example.locationshareingapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGoogleMapBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        val singleUserId = intent.getStringExtra("SINGLE_USER_ID")

        userViewModel.fetchAllUsers()
        userViewModel.users.observe(this) { userList ->
            mMap.clear()

            // একক ইউজারে ক্লিক করলে শুধুই তার Marker দেখাবে, অন্যথায় সবার Marker দেখাবে
            val filteredList = if (singleUserId != null) {
                userList.filter { it.userId == singleUserId }
            } else {
                userList
            }

            for (user in filteredList) {
                val userLocation = LatLng(user.latitude, user.longitude)
                val isCurrentLoggedUser = (user.userId == currentUid)

                // শর্ত অনুযায়ী: Logged In యూজার Marker BLUE, অন্যান্য ইউজারের Marker RED
                val markerColor = if (isCurrentLoggedUser) {
                    BitmapDescriptorFactory.HUE_BLUE
                } else {
                    BitmapDescriptorFactory.HUE_RED
                }

                val markerName = user.displayName ?: user.userEmail

                mMap.addMarker(
                    MarkerOptions()
                        .position(userLocation)
                        .title(markerName)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                )

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
            }
        }
    }
}