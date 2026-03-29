package com.readyaid.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.readyaid.ui.home.HomeScreen
import com.readyaid.ui.onboarding.OnboardingScreen

@Composable
fun ReadyAidNavGraph(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val appState by viewModel.appState.collectAsState()

    when (appState) {
        is AppState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is AppState.Onboarding -> {
            // Force onboarding until profileCompleted is true
            NavHost(navController = navController, startDestination = "onboarding") {
                composable("onboarding") {
                    OnboardingScreen(onComplete = {
                        // After save, the ViewModel will automatically update appState to Disclaimer
                    })
                }
            }
        }
        is AppState.Disclaimer -> {
            com.readyaid.ui.onboarding.DisclaimerDialog(
                onAccept = { viewModel.acceptDisclaimer() }
            )
        }
        is AppState.Home -> {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        onNavigateToChat = { navController.navigate("chat") },
                        onNavigateToSos = { navController.navigate("sos") },
                        onNavigateToGuide = { navController.navigate("guide_list") },
                        onNavigateToMyInfo = { navController.navigate("my_info") }
                    )
                }
                composable("chat") {
                    com.readyaid.ui.chat.ChatScreen(
                        onNavigateUp = { navController.popBackStack() }
                    )
                }
                composable("sos") {
                    com.readyaid.ui.sos.SosScreen(
                        onNavigateUp = { navController.popBackStack() }
                    )
                }
                composable("guide_list") {
                    com.readyaid.ui.firstaid.list.GuideListScreen(
                        onNavigateUp = { navController.popBackStack() },
                        onScenarioSelected = { id -> navController.navigate("guide_detail/$id") }
                    )
                }
                composable("guide_detail/{scenarioId}") { backStack ->
                    val id = backStack.arguments?.getString("scenarioId") ?: ""
                    com.readyaid.ui.firstaid.detail.GuideDetailScreen(
                        scenarioId = id,
                        onNavigateUp = { navController.popBackStack() },
                        onAskAI = { query ->
                            navController.navigate("chat")
                            // Note: Pre-fill handled via saved state handle in future iteration
                        }
                    )
                }
                composable("my_info") {
                    com.readyaid.ui.myinfo.MyInfoScreen(
                        onNavigateUp = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
