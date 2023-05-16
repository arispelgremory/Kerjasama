package com.gremoryyx.kerjasama

import LoginViewModelFactory
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.gremoryyx.kerjasama.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileAccountFragment : Fragment() {

    val userRepo = UserRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val loginViewModelFactory by lazy {
        LoginViewModelFactory(requireActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE))
    }
    private val loginViewModel: LoginViewModel by viewModels { loginViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton = getView()?.findViewById<Button>(R.id.profile_save)
        saveButton?.setOnClickListener {
            updateAccount()
        }

        val logoutButton = getView()?.findViewById<Button>(R.id.profile_logout)
        logoutButton?.setOnClickListener {
            loginViewModel.logoutUser()
            auth.signOut()
            Intent(context, WelcomeActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        }
    }

    private fun updateAccount(){
        val newEmail = getView()?.findViewById<TextView>(R.id.profile_email)?.text
        val newPassword = getView()?.findViewById<TextView>(R.id.profile_password)?.text
        val confirmPassword = getView()?.findViewById<TextView>(R.id.confirm_password_register)?.text
        val currentUser = userRepo.getCurrentUser()
        if (newEmail != null && newEmail != "") {
            // Update the user's email
            if (isValidEmail(newEmail.toString())) {
                // Update the user's email
                currentUser?.updateEmail(newEmail.toString())?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update the user's email in the database
                        userRepo.updateUserEmail(newEmail.toString())
                        Toast.makeText(context, "Email updated", Toast.LENGTH_LONG).show()

                        // logout if email updated
                        loginViewModel.logoutUser()
                        Intent(context, WelcomeActivity::class.java).also {
                            startActivity(it)
                            requireActivity().finish()
                        }

                    } else {
                        // Display error message
                        Toast.makeText(context, "Email update failed", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                // Display error message
                Toast.makeText(context, "Invalid email address", Toast.LENGTH_LONG).show()
            }

        }

        if (confirmPassword != "" && newPassword != "") {
            // Update the user's password
            if(newPassword.toString() == confirmPassword.toString()){
                // Update the user's password
                if (isValidPassword(newPassword.toString())){
                    currentUser?.updatePassword(newPassword.toString())
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Update the user's password in the database
                                Toast.makeText(context, "Password updated", Toast.LENGTH_LONG).show()

                                // logout if password updated
                                loginViewModel.logoutUser()
                                Intent(context, WelcomeActivity::class.java).also {
                                    startActivity(it)
                                    requireActivity().finish()
                                }

                            } else {
                                // Display error message
                                Toast.makeText(context, "Password update failed", Toast.LENGTH_LONG).show()
                            }
                        }
                }else{
                    // Display error message
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
                }

            }else{
                Log.d("newPassword!!!","$newPassword")
                Log.d("confirmPassword!!!","$confirmPassword")
                // Display error message
                Toast.makeText(context, "Password not match!", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun isValidEmail(email: String): Boolean{
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        return emailRegex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean{
        return password.length >= 6
    }

}