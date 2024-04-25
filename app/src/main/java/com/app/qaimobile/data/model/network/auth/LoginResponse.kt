package com.app.qaimobile.data.model.network.auth

data class LoginResponse(
    val access_token: String,
    val user: User
)