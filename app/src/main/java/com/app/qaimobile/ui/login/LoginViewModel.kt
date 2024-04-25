package com.app.qaimobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.R
import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.di.ResourceProvider
import com.app.qaimobile.domain.Result.Failure
import com.app.qaimobile.domain.Result.Success
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.domain.repository.UserRepository
import com.app.qaimobile.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val appDataStore: AppDataStore,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val _loginState = MutableSharedFlow<LoginState>()
    val loginState: SharedFlow<LoginState> = _loginState

    fun login(email: String, password: String, isRememberMe: Boolean) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _loginState.emit(LoginState.Error(resourceProvider.getString(R.string.please_enter_email)))
                return@launch
            }

            if (!email.isValidEmail()) {
                _loginState.emit(
                    LoginState.Error(resourceProvider.getString(R.string.please_enter_valid_email))
                )
                return@launch
            }

            if (password.isEmpty()) {
                _loginState.emit(
                    LoginState.Error(resourceProvider.getString(R.string.please_enter_password))
                )
                return@launch
            }

            if (!resourceProvider.issNetworkAvailable()) {
                _loginState.emit(LoginState.Error(resourceProvider.getString(R.string.please_check_your_internet_connection)))
                return@launch
            }

            _loginState.emit(LoginState.Loading)
            val loginRequest = LoginRequest(email, password, isRememberMe)

            when (val result = repository.login(loginRequest)) {
                is Success -> {
                    appDataStore.apply {
                        saveUserCredentials(email, password)
                        saveAccessToken(result.data.access_token)
                        saveIsLoggedIn(true)
                    }
                    _loginState.emit(LoginState.Success)
                }

                is Failure -> {
                    handleError(result.exception)
                }
            }
        }
    }

    private fun handleError(exception: Exception) {
        viewModelScope.launch {
            when (exception) {
                is IOException -> {
                    _loginState.emit(LoginState.Error(resourceProvider.getString(R.string.slower_internet_connection)))
                }

                is TimeoutException -> {
                    _loginState.emit(LoginState.Error(resourceProvider.getString(R.string.timeout_error)))
                }

                is HttpException -> {
                    if (exception.code() == 401) {
                        _loginState.emit(LoginState.Error(resourceProvider.getString(R.string.invalid_email_or_password)))
                    } else if (exception.code() == 500) {
                        _loginState.emit(LoginState.Error(resourceProvider.getString(R.string.something_went_wrong)))
                    } else {
                        _loginState.emit(LoginState.Error(resourceProvider.getString(R.string.unknown_error_occoured)))
                    }
                }

                is IllegalArgumentException -> {
                    _loginState.emit(LoginState.Error(exception.message ?: exception.toString()))
                }

                else -> {
                    _loginState.emit(LoginState.Error(exception.message ?: exception.toString()))
                }
            }
        }
    }
}
