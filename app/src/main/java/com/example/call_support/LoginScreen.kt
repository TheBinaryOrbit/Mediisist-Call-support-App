package com.example.call_support

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var fcmToken by remember { mutableStateOf("1234") } // TEMP TOKEN SET HERE

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // OPTIONAL - TRY TO GET REAL TOKEN
    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fcmToken = task.result ?: "1234"
                }
            }
    }

    val baseColor = Color(0xFF199DD1)
    val textFieldBackground = Color(0xFFF1F5F9)
    val customFont = FontFamily(Font(R.font.ubnatu_bold))

    val ambulanceAnim by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ambulanceanimation))
    val ambulanceProgress by animateLottieCompositionAsState(
        ambulanceAnim,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(baseColor),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(id = R.drawable.footerlogo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = ambulanceAnim,
                        progress = ambulanceProgress,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .aspectRatio(1f)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Login", fontSize = 30.sp, fontFamily = customFont, color = Color.Black)

                Text(
                    text = "Sign into your account",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = textFieldBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                    },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(icon, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = textFieldBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    )
                }

                 // ✅ Add this import at the top if not present



                Button(onClick = {
                    if (phone.isNotEmpty() && password.isNotEmpty()) {
                        if (fcmToken.isEmpty()) {
                            errorMessage = "Getting device token. Please wait..."
                            Log.w("LOGIN", "FCM token not yet available.")
                            return@Button
                        }

                        val loginRequest = LoginRequest(
                            phoneNumber = phone,
                            password = password,
                            fcmToken = fcmToken
                        )

                        Log.d("LOGIN", "Sending login request: $loginRequest")

                        RetrofitClient.apiService.login(loginRequest).enqueue(object : retrofit2.Callback<LoginResponse> {
                            override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                            ) {
                                Log.d("LOGIN", "Response code: ${response.code()}")
                                Log.d("LOGIN", "Raw response: ${response.raw()}")

                                val loginResponse = response.body()
                                if (response.code() == 200) {
                                    val name = loginResponse?.customerSupport?.name ?: "User"
                                    Toast.makeText(context, "Welcome $name", Toast.LENGTH_SHORT).show()
                                    Log.i("LOGIN", "Login successful")
                                    navController.navigate("home")
                                } else {
                                    errorMessage = loginResponse?.error ?: "Login failed"
                                    Log.e("LOGIN", errorMessage.toString())
                                }
                            }

                            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                errorMessage = "Network error: ${t.localizedMessage}"
                                Log.e("LOGIN", "Network request failed", t)
                            }
                        })
                    } else {
                        errorMessage = "Fill all fields"
                        Log.w("LOGIN", "Phone or password empty.")
                    }
                }) {
                    Text("Login")
                }





                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
