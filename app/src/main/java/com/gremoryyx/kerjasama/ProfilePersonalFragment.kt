package com.gremoryyx.kerjasama

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gremoryyx.kerjasama.repository.UserRepository
import kotlinx.coroutines.*
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

        val saveButton = getView()?.findViewById<TextView>(R.id.profile_save)
        saveButton?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).async {
                withContext(Dispatchers.Default) {
                    saveProfileInfo()
                    readProfileInfo()
                }
            }
        }

    }

    private fun saveProfileInfo(){
        val phoneEditText = getView()?.findViewById<TextView>(R.id.profile_phone_number)
        if (phoneEditText?.text != "" && phoneEditText?.text != userData.phone_number && phoneEditText?.length()!! >= 10) {
            userData.phone_number = phoneEditText?.text.toString()
            userRepo.updateUserPhoneNumber(userData.phone_number)
        }

        val bioEditText = getView()?.findViewById<TextView>(R.id.profile_bio)
        userData.bio = bioEditText?.text.toString()
        userRepo.updateUserBio(userData.bio)

    }



    private suspend fun saveProfilePicture(userImageUri: Uri):Unit = suspendCoroutine{ continuation->
        val storageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("User")
        val userImageRef = storageRef.child(userData.user_image)
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
                    val listView = getView()?.findViewById<ListView>(R.id.certView)
                    val adapter = ArrayAdapter<String>(requireContext(), R.layout.cert_list_item, userData.certificate)
                    listView?.adapter =adapter
                    getView()?.findViewById<TextView>(R.id.profile_bio)?.text = userData.bio
                    getView()?.findViewById<TextView>(R.id.profile_gender)?.text = userData.gender
                    getView()?.findViewById<TextView>(R.id.profile_highest_qualifications)?.text = userData.highest_qualifications
                }

            }

        }

    }

}