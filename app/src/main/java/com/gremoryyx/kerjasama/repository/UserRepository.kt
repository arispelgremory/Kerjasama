package com.gremoryyx.kerjasama.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()

    fun validateUser(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(): String {
        return auth.currentUser!!.toString()
    }

    fun getUserID(): String {
        return auth.currentUser!!.uid
    }

    suspend fun getData(){
        val userRef = db.collection("User")
        userRef.get().await().forEach {
            Log.d("UserRepository", "getData: ${it.data}")
        }
    }


}