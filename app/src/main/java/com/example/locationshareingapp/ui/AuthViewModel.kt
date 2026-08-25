package com.example.locationshareingapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// Authentication সংক্রান্ত লজিক সামলানোর ViewModel
class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _authResult = MutableLiveData<Boolean>()
    val authResult: LiveData<Boolean> get() = _authResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // ইমেইল ও পাসওয়ার্ড দিয়ে Login করার লজিক
    fun signIn(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) _authResult.value = true
                else _errorMessage.value = task.exception?.message
            }
    }

    // নতুন অ্যাকাউন্ট তৈরি (Sign Up) করার লজিক
    fun signUp(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) _authResult.value = true
                else _errorMessage.value = task.exception?.message
            }
    }
}