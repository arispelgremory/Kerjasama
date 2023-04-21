package com.gremoryyx.kerjasama

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class RegisterActivity : AppCompatActivity(), RegisterBasicInfoFragment.OnFragmentInteractionListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        if (savedInstanceState == null) {
            replaceFragment(RegisterBasicInfoFragment())
        }

        // Next button
        val nextButton: Button = findViewById(R.id.register_next_button)
        nextButton.setOnClickListener {
            var currentFragment = supportFragmentManager.findFragmentById(R.id.register_fragment_container)
            when (currentFragment) {
                is RegisterBasicInfoFragment -> {
                    replaceFragment(RegisterLoginInfoFragment())
                    replaceHeadline(getString(R.string.register_login_info))
                }

            }
        }

        // Back button
        val backButton: Button = findViewById(R.id.register_back_button)
        backButton.setOnClickListener {
            var currentFragment = supportFragmentManager.findFragmentById(R.id.register_fragment_container)
            when (currentFragment) {
                is RegisterLoginInfoFragment -> {
                    replaceFragment(RegisterBasicInfoFragment())
                    replaceHeadline(getString(R.string.register_basic_info))
                }
            }
        }


    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.register_fragment_container, fragment)
            .commit()
    }

    override fun onFragmentInteraction(data: Bundle)  {
        // Handle the received data from the fragment
        val name = data.getString("name")
        // Get other data and store them as needed
    }

    private fun replaceHeadline(headline: String) {
        findViewById<TextView>(R.id.register_basic_headline).text = headline
    }

}