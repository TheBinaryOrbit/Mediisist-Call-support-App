import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.call_support.CustomerSupportResponse
import com.example.call_support.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private var hasFetched = false

    fun fetchUserData(userId: String, context: Context) {
        if (hasFetched) return
        hasFetched = true

        RetrofitClient.apiService.getCustomerSupportById(userId)
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
                                .edit().putString("name", cs.name).apply()
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

    fun updateStatus(newStatus: Boolean, userId: String, context: Context) {
        _isLoading.value = true

        val updateRequest = mapOf("isOnline" to newStatus)

        RetrofitClient.apiService.updateOnlineStatus(userId, updateRequest)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _isOnline.value = newStatus
                        Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show()
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
}
