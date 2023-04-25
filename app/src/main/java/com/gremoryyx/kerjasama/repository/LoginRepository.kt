package com.gremoryyx.kerjasama.repository

import com.gremoryyx.kerjasama.LoginFragment

class LoginRepository {
    var loginFrag = LoginFragment()
    val currentUser = loginFrag.auth.currentUser

    fun validateUser(): Boolean {
        return currentUser != null
    }
}