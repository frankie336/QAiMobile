//com/app/qaimobile/data/model/network/auth/LoginRequest.kt
package com.app.qaimobile.data.model.network.auth

data class LoginRequest(
    val email: String,
    val password: String,
    val remember: Boolean
)