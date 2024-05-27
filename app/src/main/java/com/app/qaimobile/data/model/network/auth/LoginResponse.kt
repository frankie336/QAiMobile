package com.app.qaimobile.data.model.network.auth

import com.google.gson.annotations.SerializedName

/**
 * Represents the response received after a successful login.
 *
 * @property accessToken The token used for authentication in subsequent requests.
 * @property user The user object containing user details.
 */
data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    val user: User
)
