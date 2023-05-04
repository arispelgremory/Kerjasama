package com.gremoryyx.kerjasama

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class RegisterSetupProfilePictureFragment : Fragment() {

    private lateinit var username: String
    private var ProfilePictureListener: RegisterSetupProfilePictureFragment.OnProfilePictureFragmentInteractionListener? = null
    private val GALLERY_REQUEST_CODE = 100


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
            requestStoragePermission()
            openGallery()
        }

    }

    // Request storage permissions if needed
    private fun requestStoragePermission() {
        // Check if the permission is already granted
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        // val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        // If not, request the permission
        if (!readPermission) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }

    }


    // Open the gallery to select an image
    private fun openGallery() {
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    interface OnProfilePictureFragmentInteractionListener {
        fun onLoginInfoFragmentInteraction(data: Bundle)
    }

    public fun setUsername(text: String) {
        username = text
    }

}