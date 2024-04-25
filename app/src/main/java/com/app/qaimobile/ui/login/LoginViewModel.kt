package com.app.qaimobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.qaimobile.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.qaimobile.domain.Result.Success
import com.app.qaimobile.domain.Result.Failure
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.domain.repository.UserRepository


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val appDataStore: AppDataStore
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isEmpty()) {
            //_loginState.value = LoginState.Error("Username or password is empty")
            viewModelScope.launch {
                _loginState.value =
                    LoginState.Error("Username or password is empty: ${appDataStore.isLoggedIn()}")
            }
            return
        }

        _loginState.value = LoginState.Loading
        val user = User(email, password)
        viewModelScope.launch {
            val result = repository.login(user)
            _loginState.value = when (result) {
                is Success -> LoginState.Success
                is Failure -> LoginState.Error("Login failed: ${result.exception.message}")
            }
        }
    }
}
