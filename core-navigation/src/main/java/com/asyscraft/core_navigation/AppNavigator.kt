package com.asyscraft.core_navigation


object AppNavigator {
    private var navigator: Navigator? = null

    fun setNavigator(nav: Navigator) {
        navigator = nav
    }

    fun goToLogin() {
        navigator?.navigateToLogin()
    }
}
