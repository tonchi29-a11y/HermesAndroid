package com.hermes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hermes.app.data.preferences.AppPreferences
import com.hermes.app.ui.screens.ChatScreen
import com.hermes.app.ui.screens.SessionsScreen
import com.hermes.app.ui.screens.SettingsScreen
import com.hermes.app.ui.theme.HermesTheme
import com.hermes.app.viewmodel.ChatViewModel
import com.hermes.app.viewmodel.SessionsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = AppPreferences(this)

        setContent {
            HermesTheme {
                val navController = rememberNavController()
                val chatViewModel: ChatViewModel = viewModel()
                val sessionsViewModel: SessionsViewModel = viewModel()
                val chatState by chatViewModel.state.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = "chat",
                ) {
                    composable("chat") {
                        ChatScreen(
                            viewModel = chatViewModel,
                            onOpenSessions = { navController.navigate("sessions") },
                            onOpenSettings = { navController.navigate("settings") },
                        )
                    }

                    composable("sessions") {
                        SessionsScreen(
                            viewModel = sessionsViewModel,
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            prefs = prefs,
                            isConnected = chatState.isConnected,
                            checkingConnection = chatState.checkingConnection,
                            onCheckConnection = { chatViewModel.checkConnection() },
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
