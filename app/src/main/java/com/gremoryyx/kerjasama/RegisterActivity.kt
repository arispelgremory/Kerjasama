package com.gremoryyx.kerjasama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class RegisterActivity : AppCompatActivity(),
    RegisterLoginInfoFragment.OnLoginInfoFragmentInteractionListener,
    RegisterBasicInfoFragment.OnBasicInfoFragmentInteractionListener,
    RegisterEducationFragment.OnEducationFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (savedInstanceState == null) {
            replaceFragment(RegisterBasicInfoFragment())
        }

        // Stored user input data
        val userData = Bundle()

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
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.register_fragment_container, fragment)
            .commit()
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
