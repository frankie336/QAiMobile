package com.app.qaimobile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.app.qaimobile.ui.destinations.HomeScreenDestination
import com.app.qaimobile.ui.destinations.LoginScreenDestination
import com.app.qaimobile.ui.home.HomeScreen
import com.app.qaimobile.ui.login.LoginScreen
import com.ramcosta.composedestinations.utils.composable

const val APP_NAV_GRAPH_ROUTE = "app"

fun NavGraphBuilder.appNavGraph(navController: NavController, startDestination: String) {

    navigation(route = APP_NAV_GRAPH_ROUTE, startDestination = startDestination) {
        composable(LoginScreenDestination){
            LoginScreen(navHostController = destinationsNavigator(navController))
        }

        composable(HomeScreenDestination){
            HomeScreen(navHostController = destinationsNavigator(navController))
        }
    }

}