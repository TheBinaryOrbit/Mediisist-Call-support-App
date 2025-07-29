package com.example.call_support.domain

import android.R
import okhttp3.Address

// ✅ Accepted Ride Item model
data class AcceptedRideItem(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isLocationAvail: Boolean,
    val isCallAccepted: Boolean,
    val isRideAccepted: Boolean,
    val createdAt: String,
    val address: String
)

// ✅ Single Accepted Ride Response
data class SingleAcceptedRideResponse(
    val message: String,
    val ride:  List<AcceptedRideItem>
)
