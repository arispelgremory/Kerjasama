package com.gremoryyx.kerjasama

import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class RegisterSetupProfilePictureFragment : Fragment() {

    private lateinit var username: String
    private var ProfilePictureListener: RegisterSetupProfilePictureFragment.OnProfilePictureFragmentInteractionListener? = null
    private val GALLERY_REQUEST_CODE = 100
    private lateinit var imageUri: Uri
    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            imageUri = uri
            Log.d("RegisterSetupProfilePictureFragment", "Image URI: $uri")
            // Set the image view to the selected image
            requireView().findViewById<ImageView>(R.id.register_profile_picture).setImageURI(uri)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_setup_profile_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView()?.findViewById<TextView>(R.id.register_username_profile_label)?.text = username

        // Image on click listener to open the gallery
        getView()?.findViewById<ImageView>(R.id.register_profile_picture)?.setOnClickListener {
            getPhoto.launch("image/*")
        }

    }

     public fun sendDataToActivity(): Bundle {
        val data = Bundle()

        val userImage: Uri = imageUri
         Log.d("Send Data To Activity From Profile setup##########", "Image URI: $userImage")
        data.putString("user_image", userImage.toString())

        return data
    }


    interface OnProfilePictureFragmentInteractionListener {
        fun OnProfilePictureFragmentInteraction(data: Bundle)
    }

    public fun setUsername(text: String) {
        username = text
    }

}