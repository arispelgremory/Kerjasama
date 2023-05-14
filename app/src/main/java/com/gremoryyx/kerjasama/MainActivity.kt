package com.gremoryyx.kerjasama

import LoginViewModelFactory
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private val loginViewModelFactory by lazy {
        LoginViewModelFactory(this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE))
    }
    private val loginViewModel: LoginViewModel by viewModels { loginViewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Observe the login state LiveData and handle UI updates
        loginViewModel.isLoggedIn.observe(this, Observer { isLoggedIn ->
            if (!isLoggedIn) {
                Toast.makeText(this, "navigating to welcome", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        })

        replaceFragment(HomeFragment())

        val bottomNavigationView = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                }
                R.id.list -> {
                    replaceFragment(CourseFragment())
                }
                R.id.notification -> {
                    replaceFragment(NotificationListFragment())
                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}