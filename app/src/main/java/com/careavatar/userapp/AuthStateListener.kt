package com.careavatar.userapp

// core module
interface AuthStateListener {
    fun onLogout()
    fun onLogin()
}