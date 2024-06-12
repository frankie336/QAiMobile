package com.app.qaimobile.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.qaimobile.navigation.Destinations.APP_NAV_GRAPH_ROUTE
import com.app.qaimobile.ui.chat.QComposerScreen
import com.app.qaimobile.ui.chat.ChatViewModel
import com.app.qaimobile.ui.forgot_password.ForgotPasswordScreen
import com.app.qaimobile.ui.forgot_password.ForgotPasswordViewModel
import com.app.qaimobile.ui.home.HomeScreen
import com.app.qaimobile.ui.home.HomeViewModel
import com.app.qaimobile.ui.login.LoginScreen
import com.app.qaimobile.ui.login.LoginViewModel
import com.app.qaimobile.ui.settings.PersonalitySelectionScreen
import com.app.qaimobile.ui.splash.SplashScreen
import com.app.qaimobile.ui.vector_files.FileListScreen
import com.app.qaimobile.ui.vector_files.FileViewModel

fun NavGraphBuilder.appNavGraph(navController: NavController, startDestination: String) {
    navigation(route = APP_NAV_GRAPH_ROUTE, startDestination = startDestination) {
        composable(Destinations.LOGIN_ROUTE) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                state = loginViewModel.state.value,
                onEvent = loginViewModel::onEvent,
                uiEvent = loginViewModel.uiEvent,
                navHostController = navController
            )
        }

        composable(Destinations.HOME_ROUTE) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                onEvent = homeViewModel::onEvent,
                uiEvent = homeViewModel.uiEvent,
                navHostController = navController
            )
        }

        composable(Destinations.FORGOT_PASSWORD_ROUTE) {
            val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(
                state = forgotPasswordViewModel.state.value,
                onEvent = forgotPasswordViewModel::onEvent,
                uiEvent = forgotPasswordViewModel.uiEvent,
                navHostController = navController
            )
        }

        composable(Destinations.SPLASH_ROUTE) {
            SplashScreen()
        }

        composable(Destinations.CHAT_ROUTE) {
            val chatViewModel: ChatViewModel = hiltViewModel()
            QComposerScreen(
                onEvent = chatViewModel::onEvent,
                uiEvent = chatViewModel.uiEvent,
                navHostController = navController
            )
        }

        composable(Destinations.PERSONALITY_SELECTION_ROUTE) {
            PersonalitySelectionScreen(
                navController = navController
            )
        }

        composable(Destinations.FILE_LIST_ROUTE) {  // New composable
            val fileViewModel: FileViewModel = hiltViewModel()
            FileListScreen(navController = navController, fileViewModel = fileViewModel)
        }
    }
}
