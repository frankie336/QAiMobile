package com.app.qaimobile.data.model.network.auth

/**
 * Represents the user details included in the login response.
 *
 * @property id The unique identifier of the user.
 * @property email The email address of the user.
 * @property name The name of the user (optional).
 * @property username The username of the user.
 */
data class User(
    val id: String,
    val email: String,
    val name: String?,
    val username: String
)