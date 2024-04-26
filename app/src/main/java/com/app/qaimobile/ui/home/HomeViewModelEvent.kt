package com.app.qaimobile.ui.home

sealed class HomeViewModelEvent {
    data object Logout: HomeViewModelEvent()
}