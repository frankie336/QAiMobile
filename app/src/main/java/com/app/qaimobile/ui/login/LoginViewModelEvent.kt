package com.app.qaimobile.ui.login

sealed class LoginViewModelEvent {
    data object Authenticate: LoginViewModelEvent()
    data class UpdateEmail(val email: String): LoginViewModelEvent()
    data class UpdatePassword(val password: String): LoginViewModelEvent()
    data class UpdateRememberMe(val rememberMe: Boolean): LoginViewModelEvent()
}