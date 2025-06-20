package com.example.call_support.domain

// ✅ Accepted Ride Item model
data class AcceptedRideItem(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isLocationAvail: Boolean,
    val isCallAccepted: Boolean,
    val isRideAccepted: Boolean,
    val createdAt: String
)

// ✅ Single Accepted Ride Response
data class SingleAcceptedRideResponse(
    val message: String,
    val ride:  List<AcceptedRideItem>
)
