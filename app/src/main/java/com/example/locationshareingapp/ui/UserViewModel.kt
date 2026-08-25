package com.example.locationshareingapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locationshareingapp.data.AppUser
import com.google.firebase.firestore.FirebaseFirestore

// Firestore এবং ইউজার ডাটা হ্যান্ডেল করার ViewModel
class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _users = MutableLiveData<List<AppUser>>()
    val users: LiveData<List<AppUser>> get() = _users

    // নতুন ইউজার তথ্য Firestore-এ সেভ করা
    fun saveUserToFirestore(user: AppUser, onComplete: () -> Unit) {
        db.collection("AppUsers").document(user.userId)
            .set(user)
            .addOnSuccessListener { onComplete() }
    }

    // Firestore থেকে সকল ইউজারের ডাটা নিয়ে আসা
    fun fetchAllUsers() {
        db.collection("AppUsers").get().addOnSuccessListener { snapshot ->
            val list = snapshot.toObjects(AppUser::class.java)
            _users.value = list
        }
    }

    // প্রোফাইল থেকে Name আপডেট করা
    fun updateDisplayName(userId: String, newName: String, onComplete: () -> Unit) {
        db.collection("AppUsers").document(userId)
            .update("displayName", newName)
            .addOnSuccessListener { onComplete() }
    }
}