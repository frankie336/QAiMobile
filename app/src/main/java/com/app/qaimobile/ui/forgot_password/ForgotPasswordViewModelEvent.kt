package com.app.qaimobile.ui.forgot_password

sealed class ForgotPasswordViewModelEvent {
    data object ResetPassword: ForgotPasswordViewModelEvent()
    data class UpdateEmail(val email: String): ForgotPasswordViewModelEvent()


}