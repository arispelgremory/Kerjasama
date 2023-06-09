package com.gremoryyx.kerjasama

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.File
import kotlin.coroutines.suspendCoroutine

class RegisterActivity : AppCompatActivity(),
    RegisterLoginInfoFragment.OnLoginInfoFragmentInteractionListener,
    RegisterBasicInfoFragment.OnBasicInfoFragmentInteractionListener,
    RegisterEducationFragment.OnEducationFragmentInteractionListener,
    RegisterSetupProfilePictureFragment.OnProfilePictureFragmentInteractionListener {

    // Stored user input data
    private val userData = Bundle()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (savedInstanceState == null) {
            replaceFragment(RegisterBasicInfoFragment())
        }

        db = FirebaseFirestore.getInstance()
        // Next button
        val nextButton: Button = findViewById(R.id.register_next_button)
        nextButton.setOnClickListener {
            when (val currentFragment = supportFragmentManager.findFragmentById(R.id.register_fragment_container)) {
                // Step 1
                is RegisterBasicInfoFragment -> {
                    val data = currentFragment.sendDataToActivity()
                    userData.putAll(data)


                    //  Check if all fields are filled
                    var options = ""
                    if (userData.getString("name") == "") {
                        options += "Fill in your name,"
                    }


                    if (userData.getString("ic_number") == "") {
                        options += " IC Number,"
                    } else if (userData.getString("ic_number")!!.length != 12) {
                        options += " IC Number must be 12 digits,"
                    }

                    if (userData.getString("phone_number") == "") {
                        options += " Phone Number,"
                    } else if ((userData.getString("phone_number")!!.length != 9
                        && userData.getString("phone_number")!!.length != 10)
                        || !userData.getString("phone_number")!!.startsWith("1")
                    ) {
                        options += " Phone Number must be 9 or 10 digits and starts with 1,"
                    }

                    if (userData.getString("address") == "") {
                        options += " Address,"
                    }

                    // User didn't press the gender button
                    if (userData.getString("gender") == "Gender") {
                        if (options.isNotEmpty()) options += " and"
                        options += " Select your gender,"
                    }

                    if (userData.getString("date_of_birth") == "Choose Date") {
                        options += " Choose your Date of Birth."
                    }


                    if (options.isNotEmpty()) {
                        var message = "Please fill in the following fields: "
                        message += options
                        message = message.substring(0, message.length - 1)
                        message += "."
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    replaceFragment(RegisterLoginInfoFragment())
                    replaceHeadline(getString(R.string.register_login_info_headline))
                }
                is RegisterLoginInfoFragment -> {
                    // Step 2
                    val data = currentFragment.sendDataToActivity()
                    userData.putAll(data)

                    val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$".toRegex(RegexOption.IGNORE_CASE)
                    if(!emailPattern.matches(userData.getString("email")!!)) {
                        Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if(userData.getString("password")!!.length < 6) {
                        Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (userData.getString("password") != userData.getString("confirm_password")) {
                        Toast.makeText(this, "Password and Confirm Password must be the same", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Handle navigation to the next screen or activity
                    replaceFragment(RegisterEducationFragment())
                    replaceHeadline(getString(R.string.register_qualifications_headline))
                }
                is RegisterEducationFragment -> {
                    // Step 3
                    val data = currentFragment.sendDataToActivity()
                    userData.putAll(data)

                    if(userData.getString("highest_qualifications") == getString(R.string.highest_qualifications)) {
                        Toast.makeText(this, "Please select your highest qualification", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Handle navigation to the next screen or activity
                    replaceFragment(RegisterSetupProfilePictureFragment())
                    replaceHeadline(getString(R.string.register_profile_picture_headline))

                }
                is RegisterSetupProfilePictureFragment -> {
                    // Step 4
                    // val data = currentFragment.sendDataToActivity()
                    // userData.putAll(data)
                    // Handle navigation to the next screen or activity
                    val data = currentFragment.sendDataToActivity()
                    userData.putAll(data)



                    replaceFragment(RegisterTermsAndConditionsFragment())
                    replaceHeadline(getString(R.string.register_terms_and_conditions_headline))
                }
                is RegisterTermsAndConditionsFragment -> {
                    // Step 5
                    registerNewUsers(userData.getString("email")!!, userData.getString("password")!!)
                    showCompleteDialog()
                }
            }
        }

        // Back button
        val backButton: Button = findViewById(R.id.register_back_button)
        backButton.setOnClickListener {
            when (val currentFragment = supportFragmentManager.findFragmentById(R.id.register_fragment_container)) {
                is RegisterBasicInfoFragment -> {
                    // Handle navigation to the welcome activity
                    Intent(this, WelcomeActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                is RegisterLoginInfoFragment -> {
                    replaceFragment(RegisterBasicInfoFragment())
                    replaceHeadline(getString(R.string.register_basic_info_headline))
                    userData.clear()
                }
                is RegisterEducationFragment -> {
                    replaceFragment(RegisterLoginInfoFragment())
                    replaceHeadline(getString(R.string.register_login_info_headline))
                    // Remove the filled data
                    userData.remove("email")
                    userData.remove("password")
                    userData.remove("confirm_password")
                }
                is RegisterSetupProfilePictureFragment -> {
                    replaceFragment(RegisterEducationFragment())
                    replaceHeadline(getString(R.string.register_qualifications_headline))
                    // Remove the filled data
                    userData.remove("highest_qualification")
                }
                is RegisterTermsAndConditionsFragment -> {
                    replaceFragment(RegisterSetupProfilePictureFragment())
                    replaceHeadline(getString(R.string.register_profile_picture_headline))
                }

            }
        }
    }

    private fun showCompleteDialog() {
        val builder = AlertDialog.Builder(this)

        // Inflate custom layout
        val customView = layoutInflater.inflate(R.layout.create_successful_dialog, null)

        // Set custom layout to the dialog
        builder.setView(customView)

        // Create and show the dialog
        val alertDialog = builder.create()
        alertDialog.show()

        // Set click listener on OK button
        customView.findViewById<Button>(R.id.success_button)?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            alertDialog.dismiss()
        }
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.register_fragment_container, fragment)
            .commit()

        if(fragment is RegisterSetupProfilePictureFragment) {
            fragment.setUsername(userData.getString("name")!!)
        }
    }

    private fun replaceHeadline(headline: String) {
        findViewById<TextView>(R.id.register_basic_headline).text = headline
    }

    private suspend fun uploadImageToStorage(userImageUri:Uri):Unit = suspendCoroutine{continuation->
//        val storageRef = Firebase.storage.reference
        val storageRef = Firebase.storage("gs://kerjasama-676767.appspot.com").reference.child("User")
        val userImageRef = storageRef.child("${userData.getString("phone_number")}")

        if (userImageUri == Uri.EMPTY){
            userData.putString("user_image", userData.getString("phone_number"))
            continuation.resumeWith(Result.success(Unit))
        }else {
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
                    Log.d("Register msg!!!!!!!!!!!!", "Image uploaded successfully: $downloadUri")

                    userData.putString("user_image", userData.getString("phone_number"))
                    Log.d("Assign phonenumber to user_image", "${userData.getString("user_image")}")
                    continuation.resumeWith(Result.success(Unit))
                } else {
                    // Handle failures
                    // ...
                    continuation.resumeWith(Result.failure(task.exception!!))
                }

            }
        }

    }

    private fun registerNewUsers(email:String, password:String){
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Register msg", "createUserWithEmail:success")
                    task.result?.user?.let {
                        val newUser = UserData()
                        CoroutineScope(Dispatchers.Main).launch {
                            uploadImageToStorage(Uri.parse(userData.getString("user_image")))

                            newUser.user_image = userData.getString("user_image")!!
                            Log.d("After passed image@@@@@", "${newUser.user_image}")
                            newUser.name = userData.getString("name")!!
                            newUser.email = userData.getString("email")!!
                            newUser.gender = userData.getString("gender")!!
                            newUser.date_of_birth = userData.getString("date_of_birth")!!
                            newUser.ic_number = userData.getString("ic_number")!!
                            newUser.phone_number = "0"+userData.getString("phone_number")!!
                            newUser.highest_qualifications = userData.getString("highest_qualifications")!!
                            Log.d("User GET REGISTERDATA#####", "${newUser}")
                            Log.d("User ID#####", "${it.uid}")
                            db.collection("User").document("${it.uid}").set(newUser)
                                .addOnSuccessListener {
                                    Log.d("User", "User added to database")

                                }
                                .addOnFailureListener {
                                    Log.d("User", "Failed to add user to database")
                                }
                        }



                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Register msg", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }



    override fun onLoginInfoFragmentInteraction(data: Bundle) {
        // This method is not used anymore, but we still need to implement it to satisfy the interface requirements
    }

    override fun onBasicInfoFragmentInteraction(data: Bundle) {
        // This method is not used anymore, but we still need to implement it to satisfy the interface requirements
    }

    override fun onEducationFragmentInteraction(data: Bundle) {
        // This method is not used anymore, but we still need to implement it to satisfy the interface requirements
    }
    override fun onProfilePictureFragmentInteraction(data: Bundle) {
        // This method is not used anymore, but we still need to implement it to satisfy the interface requirements
    }

}
