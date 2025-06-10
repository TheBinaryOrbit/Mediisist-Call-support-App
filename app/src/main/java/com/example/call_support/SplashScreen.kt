package com.example.call_support

import androidx.compose.ui.Alignment
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.call_support.R

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    // Custom font define karo
    val customFont = FontFamily(Font(R.font.lobster_regular))

    LaunchedEffect(true) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1500,
                    easing = {
                        OvershootInterpolator(4f).getInterpolation(it)
                    }
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500)
            )
        }

        delay(2000)

        val isLoggedIn = isUserLoggedIn()

        navController.navigate(if (isLoggedIn) "home" else "login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F8FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logonav),
                contentDescription = "Mediisist Logo",
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//               // text = "Hum Hain Na",
//                fontSize = 26.sp,
//                color = Color.Black,
//                fontFamily = customFont, // 👈 yeh line custom font ke liye
//                modifier = Modifier.alpha(alpha.value)
//            )
        }
    }
}

fun isUserLoggedIn(): Boolean {
    return false // replace with real logic
}
