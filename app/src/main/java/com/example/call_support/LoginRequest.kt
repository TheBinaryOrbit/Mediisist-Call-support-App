package com.example.call_support

data class LoginRequest(
    val phoneNumber: String,
    val password: String,
    val fcmToken: String
)
