package com.gremoryyx.kerjasama

import SingleLiveEvent
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _isLoggedIn = SingleLiveEvent<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        _isLoggedIn.value = sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // Call this method when the user logs in successfully
    private fun loginUser(token: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("userToken", token)
        editor.apply()

        _isLoggedIn.value = true


    }

    // Call this method when the user logs out
     fun logoutUser() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.putString("userToken", null)
        editor.apply()

        _isLoggedIn.value = false
    }


    fun signInWithEmailAndPassword(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get user token here after successful login
                    val userToken = task.result?.user?.getIdToken(false)?.result?.token
                    if (userToken != null) {
                        loginUser(userToken)
                    }
                } else {
                    logoutUser()
                }
            }
    }

}