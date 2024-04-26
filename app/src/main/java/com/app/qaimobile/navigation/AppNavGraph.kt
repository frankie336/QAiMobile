package com.app.qaimobile.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.app.qaimobile.navigation.Destinations.APP_NAV_GRAPH_ROUTE
import com.app.qaimobile.ui.destinations.HomeScreenDestination
import com.app.qaimobile.ui.destinations.LoginScreenDestination
import com.app.qaimobile.ui.home.HomeScreen
import com.app.qaimobile.ui.login.LoginScreen
import com.app.qaimobile.ui.login.LoginViewModel
import com.ramcosta.composedestinations.utils.composable

fun NavGraphBuilder.appNavGraph(navController: NavController, startDestination: String) {

    navigation(route = APP_NAV_GRAPH_ROUTE, startDestination = startDestination) {
        composable(LoginScreenDestination){
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                state = loginViewModel.state.value,
                onEvent = loginViewModel::onEvent,
                uiEvent = loginViewModel.uiEvent,
                navHostController = destinationsNavigator(navController)
            )
        }

        composable(HomeScreenDestination){
            HomeScreen(navHostController = destinationsNavigator(navController))
        }
    }

}