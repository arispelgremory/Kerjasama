package com.gremoryyx.kerjasama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                    replaceFragment(ListFragment())
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