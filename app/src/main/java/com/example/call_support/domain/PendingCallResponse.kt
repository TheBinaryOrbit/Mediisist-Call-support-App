package com.example.call_support.domain



data class PendingCallResponse(
    val message: String,
    val ride: List<PendingRideItem>
)

data class PendingRideItem(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isLocationAvail: Boolean,
    val isCallAccepted: Boolean,
    val isRideAccepted: Boolean,
    val createdAt: String
)
