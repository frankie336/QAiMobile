package com.app.qaimobile.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Request location and notification permissions
        requestLocationPermission()
        requestNotificationPermission()

        setContent {
            QAiMobileTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    val isLoggedIn = dataStore.getIsLoggedIn().first()
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
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    navController = navController,
                    startDestination = APP_NAV_GRAPH_ROUTE,
                ) {
                    appNavGraph(navController, SplashScreenDestination.route)
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted
                } else {
                    // Location permission denied, handle accordingly (e.g., show an explanation or disable location-related features)
                }
            }
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Notification permission granted
                } else {
                    // Notification permission denied, handle accordingly (e.g., show an explanation or disable notification-related features)
                }
            }
        }
    }
}
