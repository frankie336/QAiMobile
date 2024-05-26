package com.app.qaimobile.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.app.qaimobile.navigation.Destinations.APP_NAV_GRAPH_ROUTE
import com.app.qaimobile.ui.chat.QComposerScreen  // Import the QComposerScreen
import com.app.qaimobile.ui.chat.ChatViewModel  // Import the ChatViewModel
import com.app.qaimobile.ui.destinations.ForgotPasswordScreenDestination
import com.app.qaimobile.ui.destinations.HomeScreenDestination
import com.app.qaimobile.ui.destinations.LoginScreenDestination
import com.app.qaimobile.ui.destinations.SplashScreenDestination
import com.app.qaimobile.ui.destinations.QComposerScreenDestination // Ensure QComposerScreen destination import
import com.app.qaimobile.ui.forgot_password.ForgotPasswordScreen
import com.app.qaimobile.ui.forgot_password.ForgotPasswordViewModel
import com.app.qaimobile.ui.home.HomeScreen
import com.app.qaimobile.ui.home.HomeViewModel
import com.app.qaimobile.ui.login.LoginScreen
import com.app.qaimobile.ui.login.LoginViewModel
import com.app.qaimobile.ui.splash.SplashScreen
import com.ramcosta.composedestinations.utils.composable

fun NavGraphBuilder.appNavGraph(navController: NavController, startDestination: String) {

    navigation(route = APP_NAV_GRAPH_ROUTE, startDestination = startDestination) {
        composable(LoginScreenDestination) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                state = loginViewModel.state.value,
                onEvent = loginViewModel::onEvent,
                uiEvent = loginViewModel.uiEvent,
                navHostController = destinationsNavigator(navController)
            )
        }

        composable(HomeScreenDestination) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                onEvent = homeViewModel::onEvent,
                uiEvent = homeViewModel.uiEvent,
                navHostController = destinationsNavigator(navController)
            )
        }

        composable(ForgotPasswordScreenDestination) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(
                state = forgotPasswordViewModel.state.value,
                onEvent = forgotPasswordViewModel::onEvent,
                uiEvent = forgotPasswordViewModel.uiEvent,
                navHostController = destinationsNavigator(navController)  // Corrected typo
            )
        }

        composable(SplashScreenDestination) {
            SplashScreen()
        }

        // Adding composable for QComposerScreen with correct type
        composable(QComposerScreenDestination) {
            val chatViewModel: ChatViewModel = hiltViewModel()
            QComposerScreen(
                //state = chatViewModel.state.value,
                onEvent = chatViewModel::onEvent,
                uiEvent = chatViewModel.uiEvent,
                navHostController = destinationsNavigator(navController)
            )
        }
    }
}