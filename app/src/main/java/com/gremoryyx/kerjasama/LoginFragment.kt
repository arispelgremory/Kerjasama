package com.gremoryyx.kerjasama

import LoginViewModelFactory
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.tasks.await



class LoginFragment : Fragment() {
    var auth: FirebaseAuth = Firebase.auth
    private val loginViewModelFactory by lazy {
        LoginViewModelFactory(requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE))
    }
    private val loginViewModel: LoginViewModel by viewModels { loginViewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText: EditText = view.findViewById(R.id.username)
        val passwordEditText: EditText = view.findViewById(R.id.password)
        val loginButton: Button = view.findViewById(R.id.login)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            validateCredentials(username, password)

        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Username and Password cannot be empty.", Toast.LENGTH_SHORT).show()
            return false
        }


        loginViewModel.signInWithEmailAndPassword(username, password)
        return true

    }
}
