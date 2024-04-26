package com.app.qaimobile.ui.login

data class LoginState(
    var email: String = "",
    var password: String = "",
    var isRememberMeChecked: Boolean = false,
    var isLoading: Boolean = false
)