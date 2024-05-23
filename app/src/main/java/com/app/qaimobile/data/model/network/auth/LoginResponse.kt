// com/app/qaimobile/data/model/network/auth/LoginResponse.kt
package com.app.qaimobile.data.model.network.auth

/**
 * Represents the response received after a successful login.
 *
 * @property access_token The token used for authentication in subsequent requests.
 * @property user The user object containing user details.
 */
data class LoginResponse(
    val access_token: String,
    val user: User
)