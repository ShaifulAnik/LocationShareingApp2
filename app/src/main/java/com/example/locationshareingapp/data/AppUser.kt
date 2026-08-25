package com.example.locationshareingapp.data

// Firestore-এ ইউজার অবজেক্ট হিসেবে সেভ করতে এবং ডাটা রিড করতে এই Data Class ব্যবহার করা হয়
data class AppUser(
    val userId: String = "",
    val userEmail: String = "",
    val displayName: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)