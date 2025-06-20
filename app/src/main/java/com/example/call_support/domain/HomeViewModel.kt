package com.example.call_support.domain

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.call_support.service.api.ApiClient
import com.example.call_support.service.api.CustomerSupportResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _isOnline = MutableStateFlow<Boolean?>(null)
    val isOnline: StateFlow<Boolean?> = _isOnline

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _pendingCalls = MutableStateFlow<List<EmergencyCall>>(emptyList())
    val pendingCalls: StateFlow<List<EmergencyCall>> = _pendingCalls

    private val _acceptedCalls = MutableStateFlow<List<EmergencyCall>>(emptyList())
    val acceptedCalls: StateFlow<List<EmergencyCall>> = _acceptedCalls

    private var hasFetched = false

    var isSwipeRefreshing by mutableStateOf(false)
        private set



    fun refreshAllCalls(userId: String, context: Context) {
        viewModelScope.launch {
            isSwipeRefreshing = true
            fetchPendingCalls(context)
            fetchActiveCalls(userId, context)
            delay(1500L)
            isSwipeRefreshing = false
        }
    }


    fun fetchUserData(userId: String, context: Context) {
        if (hasFetched) return
        hasFetched = true

        ApiClient.apiService.getCustomerSupportById(userId)
            .enqueue(object : Callback<CustomerSupportResponse> {
                override fun onResponse(
                    call: Call<CustomerSupportResponse>,
                    response: Response<CustomerSupportResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.support?.let { cs ->
                            _name.value = cs.name
                            _isOnline.value = cs.isOnline

                            context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                .edit()
                                .putString("name", cs.name)
                                .putString("userId", userId)
                                .apply()

                            fetchPendingCalls(context)
                            fetchActiveCalls(userId, context)
                        }
                    } else {
                        Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CustomerSupportResponse>, t: Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


       fun fetchPendingCalls(context: Context) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getPendingCallList()
                if (response.isSuccessful) {
                    val body = response.body()
                    val rideList = body?.ride ?: emptyList()

                    val list = rideList.map { ride ->
                        EmergencyCall(
                            id = ride.id,
                            patientName = ride.name,
                            phoneNumber = ride.phoneNumber,
                            address = "",
                            latitude = 0.0,
                            longitude = 0.0,
                            status = "pending",
                            createdAt = ride.createdAt
                        )
                    }

                    _pendingCalls.value = list
                } else {
                    Toast.makeText(context, "Failed to fetch pending calls", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error: ${e.message}", e)
            }
        }
    }

     fun fetchActiveCalls(userId: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getRidesByCustomerSupport(userId , status = "active")
                if (response.isSuccessful) {
                    val rideList = response.body()?.ride ?: emptyList()

                    val acceptedCallList = rideList.map { ride ->
                        EmergencyCall(
                            id = ride.id,
                            patientName = ride.name,
                            phoneNumber = ride.phoneNumber,
                            address = "",
                            latitude = 0.0,
                            longitude = 0.0,
                            status = "accepted",
                            createdAt = ride.createdAt
                        )
                    }

                    _acceptedCalls.value = acceptedCallList
                } else {
                    Log.e("HomeVM", "Failed to fetch accepted ride: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeVM", "Error: ${e.message}", e)
            }
        }
    }




    fun updateStatus(newStatus: Boolean, userId: String, context: Context) {
        _isLoading.value = true
        val updateRequest = mapOf("isOnline" to newStatus)

        ApiClient.apiService.updateOnlineStatus(userId, updateRequest)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _isOnline.value = newStatus
                        Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _isLoading.value = false
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun declineCall(callId: String?, context: Context) {
        if (callId.isNullOrEmpty()) return

        val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null) ?: return

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.declineRide(callId)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Call Declined", Toast.LENGTH_SHORT).show()
                    // Refresh both lists
                    fetchPendingCalls(context)
                    fetchActiveCalls(userId, context)
                } else {
                    Toast.makeText(context, "Decline failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun acceptCall(callId: String, context: Context) {
        _isLoading.value = true
        val userId = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            .getString("userId", null) ?: return

        viewModelScope.launch {
            try {
                val requestBody = mapOf("customerSupportId" to userId)
                Log.d("AcceptCall", "rideId: $callId")
                Log.d("AcceptCall", "requestBody: $requestBody")
                val response = ApiClient.apiService.acceptCallByCustomerSupport(callId, requestBody)

                if (response.isSuccessful) {
                    fetchPendingCalls(context)
                    fetchActiveCalls(userId, context)
                    Toast.makeText(context, "Call accepted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to accept call", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeCall(callId: String, context: Context) {
        _isLoading.value = true
        val userId = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            .getString("userId", null) ?: return

        viewModelScope.launch {
            try {
                val requestBody = mapOf("customerSupportId" to userId)
                Log.d("Complete Call", "rideId: $callId")
                Log.d("Complete Call", "requestBody: $requestBody")
                val response = ApiClient.apiService.completeCallByCustomerSupport(callId, requestBody)

                if (response.isSuccessful) {
                    fetchPendingCalls(context)
                    fetchActiveCalls(userId, context)
                    Toast.makeText(context, "Call Completed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to Completed call", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }
}




data class EmergencyCall(
    val id: String,
    val patientName: String,
    val phoneNumber: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val createdAt: String
)
