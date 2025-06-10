package com.example.call_support

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.call_support.ui.theme.CallSupportTheme

data class CallOverlayData(val name: String, val address: String, val phone: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge to edge and system UI setup
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            CallSupportTheme {
                val navController = rememberNavController()
                val acceptedCallsState = remember {
                    mutableStateListOf(
                        CallOverlayData("Accepted Caller", "Unknown", "0000000000")
                    )
                }

                NavHost(navController = navController, startDestination = "splash") {

                    composable("splash") {
                        SplashScreen(navController)
                    }

                    composable("login") {
                        LoginScreen(navController)
                    }

                    composable("home") {
                        MainScreen(
                            navController = navController,
                            acceptedCalls = acceptedCallsState,
                            onCancelCall = { call ->
                                acceptedCallsState.removeIf {
                                    it.name == call.name &&
                                            it.address == call.address &&
                                            it.phone == call.phone
                                }
                            }
                        )
                    }

                    composable("acceptedCalls") {
                        AcceptedCallsScreen(
                            navController = navController,
                            acceptedCalls = acceptedCallsState,
                            onCancelCall = { call ->
                                acceptedCallsState.removeIf {
                                    it.name == call.name &&
                                            it.address == call.address &&
                                            it.phone == call.phone
                                }
                            }
                        )
                    }

                    composable("myAccount") {
                        MyAccountScreen(navController = navController)
                    }

                    composable("privacy") {
                        PrivacyPolicyScreen(navController = navController)
                    }

                    composable("terms") {
                        TermsAndConditionsScreen(navController = navController)
                    }

                    // Profile Screen added here
                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            acceptedCalls = acceptedCallsState,
                            onCancelCall = { call ->
                                acceptedCallsState.removeIf {
                                    it.name == call.name &&
                                            it.address == call.address &&
                                            it.phone == call.phone
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
