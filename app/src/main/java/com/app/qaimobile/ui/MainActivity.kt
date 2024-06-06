package com.app.qaimobile.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Install the splash screen
        installSplashScreen()

        // Create notification channel
        createNotificationChannel()

        // Request notification permission
        requestNotificationPermission()

        setContent {
            QAiMobileTheme {
                val navController = rememberNavController()
                val startDestination = remember { mutableStateOf(SplashScreenDestination.route) }

                // Determine the start destination based on login status
                LaunchedEffect(Unit) {
                    val isLoggedIn = dataStore.getIsLoggedIn().first()
                    startDestination.value = if (isLoggedIn) {
                        HomeScreenDestination.route
                    } else {
                        LoginScreenDestination.route
                    }
                    navController.navigate(startDestination.value) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId)
                    }
                }

                // Set up navigation host
                NavHost(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    navController = navController,
                    startDestination = APP_NAV_GRAPH_ROUTE,
                ) {
                    appNavGraph(navController, startDestination.value)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save any necessary state information here
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore any saved state information here
    }

    /**
     * Creates a notification channel for displaying messages from Q.
     * This method is required for API 26+ (Oreo and above) to display notifications.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Message Channel"
            val descriptionText = "Channel for Q messages"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Q_MESSAGES", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Requests the notification permission for Android 13+ (API 33+) devices.
     */
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

    /**
     * Displays a notification with the specified title and message.
     *
     * @param title The title of the notification.
     * @param message The content of the notification.
     */
    private fun showNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(this, "Q_MESSAGES")
            .setSmallIcon(android.R.drawable.ic_dialog_info)  // Using a built-in Android icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1, notificationBuilder.build())
        }
    }
}