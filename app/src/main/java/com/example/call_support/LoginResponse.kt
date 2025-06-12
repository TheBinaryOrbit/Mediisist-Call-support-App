package com.example.call_support

data class LoginResponse(
    val token: String?,                     // Optional token
    val message: String?,                 // Message from server
    val error: String?,
    val status : Int,
    val customerSupport: CustomerSupport?   // Nested object
)

//data class Customersupport(
//    val id: String,
//    val name: String,
//    val phoneNumber: String,
//    val email: String
//)


