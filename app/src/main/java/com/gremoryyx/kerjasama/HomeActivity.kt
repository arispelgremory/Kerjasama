package com.gremoryyx.kerjasama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.home_fragment_container, HomeFragment())
                .commit()
        }
    }
}