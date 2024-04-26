package com.app.qaimobile.ui.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
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
    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            val userCredentials = appDataStore.userCredentials.first()
            _state.value = state.value.copy(
                email = userCredentials.first ?: "",
                password = userCredentials.second ?: ""
            )
        }
    }


    fun onEvent(event: LoginViewModelEvent) {
        when (event) {
            LoginViewModelEvent.Authenticate -> login(
                state.value.email,
                state.value.password,
                state.value.isRememberMeChecked
            )

            is LoginViewModelEvent.UpdateEmail -> _state.value =
                state.value.copy(email = event.email)

            is LoginViewModelEvent.UpdatePassword -> _state.value =
                state.value.copy(password = event.password)

            is LoginViewModelEvent.UpdateRememberMe -> _state.value =
                state.value.copy(isRememberMeChecked = event.rememberMe)
        }
    }

    private fun login(email: String, password: String, isRememberMe: Boolean) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.please_enter_email)))
                return@launch
            }

            if (!email.isValidEmail()) {
                _uiEvent.emit(
                    LoginUiEvent.ShowError(resourceProvider.getString(R.string.please_enter_valid_email))
                )
                return@launch
            }

            if (password.isEmpty()) {
                _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.please_enter_password)))
                return@launch
            }

            if (!resourceProvider.issNetworkAvailable()) {
                _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.please_check_your_internet_connection)))
                return@launch
            }


            val loginRequest = LoginRequest(email, password, isRememberMe)
            _state.value = state.value.copy(isLoading = true)
            val result = repository.login(loginRequest)
            _state.value = state.value.copy(isLoading = false)
            when (result) {
                is Success -> {
                    appDataStore.apply {
                        if (isRememberMe)
                            saveUserCredentials(email, password)
                        saveAccessToken(result.data.access_token)
                        saveIsLoggedIn(true)
                    }
                    _uiEvent.emit(LoginUiEvent.Success)
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
                    _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.slower_internet_connection)))
                }

                is TimeoutException -> {
                    _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.timeout_error)))
                }

                is HttpException -> {
                    if (exception.code() == 401) {
                        _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.invalid_email_or_password)))
                    } else if (exception.code() == 500) {
                        _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.something_went_wrong)))
                    } else {
                        _uiEvent.emit(LoginUiEvent.ShowError(resourceProvider.getString(R.string.unknown_error_occoured)))
                    }
                }

                is IllegalArgumentException -> {
                    _uiEvent.emit(LoginUiEvent.ShowError(exception.message ?: exception.toString()))
                }

                else -> {
                    _uiEvent.emit(LoginUiEvent.ShowError(exception.message ?: exception.toString()))
                }
            }
        }
    }
}

sealed class LoginUiEvent {
    data class ShowError(val message: String) : LoginUiEvent()
    data object Success : LoginUiEvent()
}
