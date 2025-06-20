package com.example.call_support.service.api

data class LoginResponse(
    val token: String?,                     // Optional token
    val message: String?,                 // Message from server
    val error: String?,
    val status : Int,
    val customerSupport: CustomerSupport?   // Nested object
)