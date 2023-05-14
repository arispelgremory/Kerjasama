package com.gremoryyx.kerjasama.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gremoryyx.kerjasama.UserData
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.net.URI
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()

    fun validateUser(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(): FirebaseUser {
        return auth.currentUser!!
    }

    fun getUserID(): String {
        return auth.currentUser!!.uid
    }

    fun getUserRef(): DocumentReference {
        return db.collection("User").document(getUserID())
    }

    suspend fun getImageFile(userImage: String): Bitmap = suspendCoroutine { continuation ->
        try{
            val userStorageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("User")
            val userImgRef = userStorageRef.child(userImage)
            var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            Log.d("IMAGE#######", "getImageFile: $userImgRef")
            userImgRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { data ->
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                    continuation.resume(bitmap)
                }
                .addOnFailureListener { exception ->
                    Log.d("getImageFailed!!!!", "getImageFile: ${exception.message}")
                    continuation.resumeWithException(exception)
                }
        }catch (e: Exception){
            Log.d("getImageFailed!!!!", "Never upload profile image: ${e.message}")
            continuation.resumeWithException(e)
        }

    }

    suspend fun getData(userId:String, userData: UserData){
        val userRef = db.collection("User")
        Log.d("UserRepository", "userRef id: ${userRef.id}")
        Log.d("UserRepository", "userdocument path: ${userRef.document(userId).path}")
        userRef.document(userId).get().await().data?.let {
            Log.d("UserRepository", "getData: $it")
            it.get("user_image")?.let { user_image -> userData.user_image = user_image.toString() }
            it.get("name")?.let { name -> userData.name = name.toString() }
            it.get("phone_number")?.let { phone_number -> userData.phone_number = phone_number.toString() }
            it.get("ic_number")?.let { ic_number -> userData.ic_number = ic_number.toString() }
            it.get("gender")?.let { gender -> userData.gender = gender.toString() }
            it.get("bio")?.let { bio -> userData.bio = bio.toString() }
            it.get("certificate")?.let{ certificate -> userData.certificate = certificate as ArrayList<String> }
            it.get("highest_qualifications")?.let { highest_qualifications -> userData.highest_qualifications = highest_qualifications.toString() }
        }

    }

    fun updateUserEmail(newEmail:String){
        val userRef = db.collection("User").document(getUserID())
        userRef.update("email", newEmail)
    }

    fun updateUserPhoneNumber(newPhoneNum:String){
        val userRef = db.collection("User").document(getUserID())
        userRef.update("phone_number", newPhoneNum)
    }

    fun updateUserBio(newBio:String){
        val userRef = db.collection("User").document(getUserID())
        userRef.update("bio", newBio)
    }


}