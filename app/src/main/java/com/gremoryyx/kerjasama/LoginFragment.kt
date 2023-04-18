package com.gremoryyx.kerjasama

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {

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

            if (validateCredentials(username, password)) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        // Add your logic to validate the credentials here.
        // You might need to call an API or check against a database.
        return username.isNotEmpty() && password.isNotEmpty()
    }
}
