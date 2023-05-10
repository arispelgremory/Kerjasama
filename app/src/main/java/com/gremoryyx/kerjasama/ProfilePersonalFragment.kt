package com.gremoryyx.kerjasama

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gremoryyx.kerjasama.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.coroutines.suspendCoroutine

class ProfilePersonalFragment : Fragment() {
    private val userRepo = UserRepository()
    private var userData = UserData()

    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            requireView().findViewById<ImageView>(R.id.profile_picture).setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_personal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // getView()?.findViewById<TextView>(R.id.profile_username)?.text = username

        //Retrieve user data
        readProfileInfo()

        // Save profile picture on click listener
        val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedUri ->
                Log.d("ProfilePersonalFragment", "Image URI: $selectedUri")
                CoroutineScope(Dispatchers.IO).launch {
                    saveProfilePicture(selectedUri)

                    //Refresh purpose
                    readProfileInfo()
                }
            }
        }

        // Image on click listener to open the gallery
        getView()?.findViewById<ImageView>(R.id.profile_picture)?.setOnClickListener {
            getPhoto.launch("image/*")
        }

    }


    private suspend fun saveProfilePicture(userImageUri: Uri):Unit = suspendCoroutine{ continuation->
        val storageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("User")
        val userImageRef = storageRef.child("${userData.user_image}")
        Log.d("img msg!!!!!!!!!!!!", "Image Ref: $userImageRef")
        val uploadTask = userImageRef.putFile(userImageUri)

        // Get the URL of the uploaded image file
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            userImageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d("Updated img msg!!!!!!!!!!!!", "Image uploaded successfully: $downloadUri")
                continuation.resumeWith(Result.success(Unit))
            } else {
                // Handle failures
                // ...
                continuation.resumeWith(Result.failure(task.exception!!))
            }

        }

    }

    private fun readProfileInfo(){
        var userId = userRepo.getUserID()
        CoroutineScope(Dispatchers.IO).launch {
            userRepo.getData(userId, userData)

            requireActivity().runOnUiThread {
                CoroutineScope(Dispatchers.Main).launch {
                    try{
                        val bitmap = userRepo.getImageFile(userData.user_image)
                        Log.d("After getimagefile##############!!!!!!!!!!", "$bitmap")
                        if (bitmap != Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)) {
                            Log.d("ProfilePersonalFragment!!!!!!!!!!", "Bitmap is not null")
                            getView()?.findViewById<ImageView>(R.id.profile_picture)
                                ?.setImageBitmap(bitmap)
                        } else {
                            Log.d("ProfilePersonalFragment!!!!!!!!!!", "Bitmap is null")
                        }
                    }catch (e: Exception) {
                        Log.d("ProfilePersonalFragment NEVER UPLOAD IMAGE!!!!!!!!!!", "Exception: $e")
                    }


                    getView()?.findViewById<TextView>(R.id.profile_username)?.text = userData.name
                    getView()?.findViewById<TextView>(R.id.profile_ic_number)?.text = userData.ic_number
                    getView()?.findViewById<TextView>(R.id.profile_phone_number)?.text = userData.phone_number
                    getView()?.findViewById<TextView>(R.id.profile_gender)?.text = userData.gender
                    getView()?.findViewById<TextView>(R.id.profile_certification)?.text = userData.highest_qualifications
                }

            }

        }

    }

}