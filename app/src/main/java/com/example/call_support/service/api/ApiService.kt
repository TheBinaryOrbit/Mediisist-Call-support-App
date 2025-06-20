package com.example.call_support.service.api

import com.example.call_support.domain.PendingCallResponse
import com.example.call_support.domain.SingleAcceptedRideResponse
import com.example.call_support.myui.profile.ChangePasswordResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 🔐 Login
    @POST("api/v1/customersupport/customersupportlogin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // 🔄 Change password
    @PATCH("api/v1/customersupport/changecustomersupportpassword/{id}")
    fun changePassword(
        @Path("id") userId: String,
        @Body body: Map<String, String>
    ): Call<ChangePasswordResponse>

    // 🟢 Update online/offline status
    @PATCH("api/v1/customersupport/changestatus/{id}")
    fun updateOnlineStatus(
        @Path("id") userId: String,
        @Body body: Map<String, Boolean>
    ): Call<Void>

    // 👤 Get customer support profile by ID
    @GET("api/v1/customersupport/getcustomersupport/{id}")
    fun getCustomerSupportById(@Path("id") id: String): Call<CustomerSupportResponse>

    // 📞 Get pending call list
    @GET("api/v1/ride/getpendingcalllist")
    suspend fun getPendingCallList(): Response<PendingCallResponse>

    // ✅ Accept call (patch)
    @PATCH("api/v1/ride/accept/customersupport/{rideId}")
    suspend fun acceptCallByCustomerSupport(
        @Path("rideId") rideId: String,
        @Body requestBody: Map<String, String>
    ): Response<Any>


    @GET("api/v1/ride/getcustomersupportride/{id}")
    suspend fun getRidesByCustomerSupport(
        @Path("id") userId: String,
        @Query("status") status: String // active OR complete
    ): Response<SingleAcceptedRideResponse>



    // ✅ Accept call (patch)
    @PATCH("api/v1/ride/complete/customersupport/{rideId}")
    suspend fun completeCallByCustomerSupport(
        @Path("rideId") rideId: String,
        @Body requestBody: Map<String, String>
    ): Response<Any>

    @DELETE("/api/v1/ride/decline/{rideId}")
    suspend fun declineRide(@Path("rideId") rideId: String): Response<Unit>

    @GET("api/v1/ride/sendsms/{rideId}")
    suspend fun sendSMS(
        @Path("rideId") rideId: String
    ): Response<Void>


}
