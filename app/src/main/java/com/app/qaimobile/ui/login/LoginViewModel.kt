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
import com.app.qaimobile.domain.repository.UserRepository


@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Username or password is empty")
            return
        }

        _loginState.value = LoginState.Loading
        val user = User(username, password)
        viewModelScope.launch {
            val result = repository.login(user)
            _loginState.value = when (result) {
                is Success -> LoginState.Success
                is Failure -> LoginState.Error("Login failed: ${result.exception.message}")
            }
        }
    }
}
