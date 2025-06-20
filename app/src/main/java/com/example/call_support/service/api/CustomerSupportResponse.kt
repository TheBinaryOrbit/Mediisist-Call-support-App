package com.example.call_support.service.api

data class CustomerSupportResponse(
    val message: String,
    val support: CustomerSupport
)

data class CustomerSupport(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val isOnline: Boolean
)
