package com.gremoryyx.kerjasama

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class RegisterLoginInfoFragment : Fragment() {

    private var loginInfoListener: OnLoginInfoFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginInfoFragmentInteractionListener) {
            loginInfoListener = context
        } else {
            throw RuntimeException("$context must implement OnLoginInfoInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginInfoListener = null
    }

    // In RegisterLoginInfoFragment
    public fun sendDataToActivity(): Bundle {
        val data = Bundle()

        val emailEditText: EditText = requireView().findViewById(R.id.email_register)
        val passwordEditText: EditText = requireView().findViewById(R.id.password_register)
        val confirmPasswordEditText: EditText = requireView().findViewById(R.id.confirm_password_register)

        data.putString("email", emailEditText.text.toString())
        data.putString("password", passwordEditText.text.toString())
        data.putString("confirm_password", confirmPasswordEditText.text.toString())

        return data
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_login_info, container, false)
    }

    interface OnLoginInfoFragmentInteractionListener {
        fun onLoginInfoFragmentInteraction(data: Bundle)
    }



}