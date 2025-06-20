package com.example.call_support.service.api

data class LoginRequest(
    val phoneNumber: String,
    val password: String,
    val fcmToken: String
)