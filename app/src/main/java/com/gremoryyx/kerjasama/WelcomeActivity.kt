package com.gremoryyx.kerjasama

import LoginViewModelFactory
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.tasks.await


class WelcomeActivity : AppCompatActivity() {
    private val loginViewModelFactory by lazy {
        LoginViewModelFactory(this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE))
    }
    private val loginViewModel: LoginViewModel by viewModels { loginViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Observe the login state LiveData and handle UI updates
        loginViewModel.isLoggedIn.observe(this, Observer { isLoggedIn ->
            if (isLoggedIn) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "not logged in", Toast.LENGTH_SHORT).show()
            }
        })

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