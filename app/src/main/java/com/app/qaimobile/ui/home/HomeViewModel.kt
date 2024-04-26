package com.app.qaimobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import com.app.qaimobile.R
import com.app.qaimobile.di.ResourceProvider
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appDataStore: AppDataStore,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            appDataStore.apply {
                saveAccessToken("")
                saveIsLoggedIn(false)
            }
            _uiEvent.emit(HomeUiEvent.ShowMessage(resourceProvider.getString(R.string.logged_out_successfully)))
            _uiEvent.emit(
                HomeUiEvent.Navigate(
                    Destinations.LOGIN_ROUTE,
                ){
                    popUpTo(Destinations.LOGIN_ROUTE) {
                        inclusive = true
                    }
                }
            )
        }
    }

}

sealed class HomeUiEvent {
    data class Navigate(
        val route: String,
        val navOptionsBuilder: NavOptionsBuilder.() -> Unit = {}
    ) : HomeUiEvent()

    data class ShowMessage(val message: String) : HomeUiEvent()
}