package com.gremoryyx.kerjasama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val registerButton = findViewById<MaterialButton>(R.id.home_register_btn)
        val loginButton = findViewById<MaterialButton>(R.id.home_login_btn)

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            // End of the welcome activity
            finish()
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}