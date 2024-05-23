package com.app.qaimobile.ui.forgot_password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptionsBuilder
import com.app.qaimobile.R
import com.app.qaimobile.di.ResourceProvider
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.domain.repository.UserRepository
import com.app.qaimobile.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: UserRepository,
    private val appDataStore: AppDataStore,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _state = mutableStateOf(ForgotPasswordState())
    val state: State<ForgotPasswordState> = _state

    private val _uiEvent = MutableSharedFlow<ForgotPasswordUiEvent>()
    val uiEvent: SharedFlow<ForgotPasswordUiEvent> = _uiEvent.asSharedFlow()

    fun onEvent(event: ForgotPasswordViewModelEvent) {
        when (event) {
            is ForgotPasswordViewModelEvent.ResetPassword -> resetPassword(state.value.email)
            is ForgotPasswordViewModelEvent.UpdateEmail -> updateEmail(event.email)
        }
    }

    private fun resetPassword(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _uiEvent.emit(ForgotPasswordUiEvent.ShowError(resourceProvider.getString(R.string.please_enter_email)))
                return@launch
            }

            if (!email.isValidEmail()) {
                _uiEvent.emit(ForgotPasswordUiEvent.ShowError(resourceProvider.getString(R.string.please_enter_valid_email)))
                return@launch
            }

            try {
                //repository.resetPassword(email)
                _uiEvent.emit(ForgotPasswordUiEvent.Success)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateEmail(email: String) {
        _state.value = state.value.copy(email = email)
    }

    private fun handleError(exception: Exception) {
        viewModelScope.launch {
            when (exception) {
                is IOException -> {
                    _uiEvent.emit(ForgotPasswordUiEvent.ShowError(resourceProvider.getString(R.string.slower_internet_connection)))
                }
                is TimeoutException -> {
                    _uiEvent.emit(ForgotPasswordUiEvent.ShowError(resourceProvider.getString(R.string.timeout_error)))
                }
                is HttpException -> {
                    if (exception.code() == 401) {
                        _uiEvent.emit(ForgotPasswordUiEvent.ShowError(resourceProvider.getString(R.string.invalid_email_or_password)))
                    } else if (exception.code() == 500) {
                        _uiEvent.emit(ForgotPasswordUiEvent.ShowError(resourceProvider.getString(R.string.something_went_wrong)))
                    } else {
                        _uiEvent.emit(ForgotPasswordUiEvent.ShowError(resourceProvider.getString(R.string.unknown_error_occurred)))  // Corrected spelling
                    }
                }
                is IllegalArgumentException -> {
                    _uiEvent.emit(ForgotPasswordUiEvent.ShowError(exception.message ?: exception.toString()))
                }
                else -> {
                    _uiEvent.emit(ForgotPasswordUiEvent.ShowError(exception.message ?: exception.toString()))
                }
            }
        }
    }
}

sealed class ForgotPasswordUiEvent {
    data class ShowError(val message: String) : ForgotPasswordUiEvent()
    object Success : ForgotPasswordUiEvent()
    data class Navigate(
        val route: String,
        val navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
    ) : ForgotPasswordUiEvent()
}