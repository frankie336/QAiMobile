package com.app.qaimobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.navigation.Destinations.APP_NAV_GRAPH_ROUTE
import com.app.qaimobile.navigation.appNavGraph
import com.app.qaimobile.ui.destinations.HomeScreenDestination
import com.app.qaimobile.ui.destinations.LoginScreenDestination
import com.app.qaimobile.ui.theme.QAiMobileTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QAiMobileTheme {
                val navController = rememberNavController()
                /*val startDestination = if (dataStore.isLoggedIn.first()) {
                    HomeScreenDestination.route
                } else {
                    LoginScreenDestination.route
                }*/
                NavHost(
                    modifier =
                    Modifier.background(
                        MaterialTheme.colorScheme.background
                    ),
                    navController = navController,
                    startDestination = APP_NAV_GRAPH_ROUTE,
                ) {
                    appNavGraph(navController, LoginScreenDestination.route)
                }
            }
        }
    }
}