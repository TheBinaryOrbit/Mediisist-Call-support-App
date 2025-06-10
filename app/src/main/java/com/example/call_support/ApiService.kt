package com.example.call_support

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST





// Retrofit API service interface
interface ApiService {

    @POST("api/v1/customersupport/customersupportlogin")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
