package com.example.call_support

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Path

// Retrofit API service interface
interface ApiService {

    @POST("api/v1/customersupport/customersupportlogin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @PATCH("api/v1/customersupport/changecustomersupportpassword/{id}")
    fun changePassword(
        @Path("id") userId: String,
        @Body body: Map<String, String>
    ): Call<ChangePasswordResponse>

    @PATCH("api/v1/customersupport/changestatus/{id}")
    fun updateOnlineStatus(
        @Path("id") userId: String,
        @Body body: Map<String, Boolean>
    ): Call<Void>


    @GET("api/v1/customersupport/getcustomersupport/{id}")
    fun getCustomerSupportById(@Path("id") id: String): Call<CustomerSupportResponse>






}
