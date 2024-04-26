package com.app.qaimobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.navigation.Destinations.APP_NAV_GRAPH_ROUTE
import com.app.qaimobile.navigation.appNavGraph
import com.app.qaimobile.ui.destinations.HomeScreenDestination
import com.app.qaimobile.ui.destinations.LoginScreenDestination
import com.app.qaimobile.ui.destinations.SplashScreenDestination
import com.app.qaimobile.ui.theme.QAiMobileTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            QAiMobileTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    val isLoggedIn = dataStore.isLoggedIn.first()
                    val startDestination = if (isLoggedIn) {
                        HomeScreenDestination.route
                    } else {
                        LoginScreenDestination.route
                    }
                    navController.navigate(startDestination) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId)
                    }
                }
                NavHost(
                    modifier =
                    Modifier.background(
                        MaterialTheme.colorScheme.background
                    ),
                    navController = navController,
                    startDestination = APP_NAV_GRAPH_ROUTE,
                ) {
                    appNavGraph(navController, SplashScreenDestination.route)
                }
            }
        }
    }
}