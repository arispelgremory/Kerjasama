package com.gremoryyx.kerjasama

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class RegisterActivity : AppCompatActivity(),
    RegisterLoginInfoFragment.OnLoginInfoFragmentInteractionListener,
    RegisterBasicInfoFragment.OnBasicInfoFragmentInteractionListener,
    RegisterEducationFragment.OnEducationFragmentInteractionListener,
    RegisterSetupProfilePictureFragment.OnProfilePictureFragmentInteractionListener {

    // Stored user input data
    private val userData = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (savedInstanceState == null) {
            replaceFragment(RegisterTermsAndConditionsFragment())
        }

        // Next button
        val nextButton: Button = findViewById(R.id.register_next_button)
        nextButton.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.register_fragment_container)
            when (currentFragment) {
                // Step 1
                is RegisterBasicInfoFragment -> {
                    val data = currentFragment.sendDataToActivity()
                    userData.putAll(data)
                    replaceFragment(RegisterLoginInfoFragment())
                    replaceHeadline(getString(R.string.register_login_info_headline))
                }
                is RegisterLoginInfoFragment -> {
                    // Step 2
                    val data = currentFragment.sendDataToActivity()
                    userData.putAll(data)
                    onLoginInfoFragmentInteraction(userData)
                    // Handle navigation to the next screen or activity
                    replaceFragment(RegisterEducationFragment())
                    replaceHeadline(getString(R.string.register_qualifications_headline))
                }
                is RegisterEducationFragment -> {
                    // Step 3
                    val data = currentFragment.sendDataToActivity()
                    userData.putAll(data)
                    onEducationFragmentInteraction(userData)
                    // Handle navigation to the next screen or activity
                    replaceFragment(RegisterSetupProfilePictureFragment())
                    replaceHeadline(getString(R.string.register_profile_picture_headline))

                }
                is RegisterSetupProfilePictureFragment -> {
                    // Step 4
                    // val data = currentFragment.sendDataToActivity()
                    // userData.putAll(data)
                    // Handle navigation to the next screen or activity
                    replaceFragment(RegisterTermsAndConditionsFragment())
                    replaceHeadline(getString(R.string.register_terms_and_conditions_headline))
                }
                is RegisterTermsAndConditionsFragment -> {
                    showCompleteDialog()
                }
            }
        }

        // Back button
        val backButton: Button = findViewById(R.id.register_back_button)
        backButton.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.register_fragment_container)
            when (currentFragment) {
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
                }
                is RegisterEducationFragment -> {
                    replaceFragment(RegisterLoginInfoFragment())
                    replaceHeadline(getString(R.string.register_login_info_headline))
                }
                is RegisterSetupProfilePictureFragment -> {
                    replaceFragment(RegisterEducationFragment())
                    replaceHeadline(getString(R.string.register_qualifications_headline))
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
            fragment.setUsername(userData.getString("username")!!)
        }
    }

    private fun replaceHeadline(headline: String) {
        findViewById<TextView>(R.id.register_basic_headline).text = headline
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
}
