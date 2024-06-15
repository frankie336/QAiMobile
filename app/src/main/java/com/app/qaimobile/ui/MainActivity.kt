package com.app.qaimobile.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.qaimobile.data.model.network.LocationUpdateRequest
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.data.remote.FileUploadService
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.navigation.Destinations.APP_NAV_GRAPH_ROUTE
import com.app.qaimobile.navigation.appNavGraph
import com.app.qaimobile.ui.destinations.HomeScreenDestination
import com.app.qaimobile.ui.destinations.LoginScreenDestination
import com.app.qaimobile.ui.destinations.SplashScreenDestination
import com.app.qaimobile.ui.image_handling.ImageHandlingScreen
import com.app.qaimobile.ui.theme.QAiMobileTheme
import com.app.qaimobile.util.location.LocationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2
private const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 3

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStore: AppDataStore

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var fileUploadService: FileUploadService // Inject FileUploadService

    @Inject
    lateinit var locationManager: LocationManager

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
                    composable("imageHandling/{threadId}") { backStackEntry ->
                        val threadId = backStackEntry.arguments?.getString("threadId")
                        ImageHandlingScreen(fileUploadService = fileUploadService, threadId = threadId) // Pass the threadId to ImageHandlingScreen
                    }
                }
            }
        }

        lifecycleScope.launch {
            val token = dataStore.getAccessToken().first()
            if (token != null) {
                startLocationUpdates(token)
            } else {
                Log.e("MainActivity", "Access token is null")
            }
        }
    }

    private fun requestLocationPermission() {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
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
                    if (permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
                        )
                    }
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
            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Background location permission granted
                } else {
                    // Background location permission denied, handle accordingly (e.g., show an explanation or disable background location-related features)
                }
            }
        }
    }

    private fun startLocationUpdates(token: String) {
        locationManager.startLocationUpdates(token) { location ->
            // Log the location
            Log.d("MainActivity", "Location updated: $location")

            // Save the location to the backend SQL table
            val locationUpdateRequest = LocationUpdateRequest(
                permissionStatus = "granted", // or appropriate status
                locationType = "foreground", // or appropriate type
                latitude = location.latitude,
                longitude = location.longitude,
                altitude = location.altitude
            )

            lifecycleScope.launch {
                try {
                    val response = apiService.updateLocation(locationUpdateRequest)
                    if (response.isSuccessful) {
                        Log.d("MainActivity", "Location updated on server: ${response.body()?.message}")
                    } else {
                        Log.e("MainActivity", "Failed to update location on server: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error updating location on server", e)
                }
            }
        }
    }
}
