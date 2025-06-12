package com.example.call_support

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.call_support.ui.theme.CallSupportTheme
import com.google.firebase.messaging.FirebaseMessaging
import mainScreenNavGraph


class MainActivity : ComponentActivity() {

    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // ✅ Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }


        // ✅ Get Firebase token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM_TOKEN", "Token: $token")
            } else {
                Log.e("FCM_TOKEN", "Token fetch failed: ${task.exception?.message}")
            }
        }

        // ✅ Handle SHOW_OVERLAY action
        intent?.let {
            if (it.action == "com.example.call_support.SHOW_OVERLAY") {
                val name = it.getStringExtra("name") ?: "Unknown"
                val phone = it.getStringExtra("phone") ?: "0000000000"
                val serviceIntent = Intent(this, OverlayService::class.java).apply {
                    putExtra("name", name)
                    putExtra("phone", phone)
                }
                ContextCompat.startForegroundService(this, serviceIntent)
            }
        }

        // ✅ Compose UI starts here
        setContent {
            CallSupportTheme {
                val context = LocalContext.current
                var hasOverlayPermission by remember {
                    mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            Settings.canDrawOverlays(context)
                        else true
                    )
                }

               
                    val navController = rememberNavController()
                    val acceptedCallsState = remember {
                        mutableStateListOf(
                            CallOverlayData("Accepted Caller", "Unknown", "0000000000")
                        )
                    }

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") { SplashScreen(navController) }
                        composable("login") { LoginScreen(navController) }
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
                        composable("myAccount") { MyAccountScreen(navController = navController) }
                        composable("privacy") { PrivacyPolicyScreen(navController = navController) }
                        composable("terms") { TermsAndConditionsScreen(navController = navController) }

                        mainScreenNavGraph(
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

//    @Composable
//    fun RequestOverlayScreen(onGranted: () -> Unit) {
//        val context = LocalContext.current
//
//        LaunchedEffect(Unit) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                !Settings.canDrawOverlays(context)
//            ) {
//                val intent = Intent(
//                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:${context.packageName}")
//                )
//                context.startActivity(intent)
//            } else {
//                onGranted()
//            }
//        }
//
//        Text("Requesting overlay permission...")
//    }
//    }

