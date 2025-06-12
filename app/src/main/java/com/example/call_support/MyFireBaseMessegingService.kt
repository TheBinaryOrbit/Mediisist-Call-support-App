import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.call_support.OverlayService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject
import kotlin.jvm.java

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data["message"]?.let { messageJson ->
            try {
                val json = JSONObject(messageJson)
                val name = json.getString("name")
                val phone = json.getString("phoneNumber")

                val serviceIntent = Intent(this, OverlayService::class.java).apply {
                    putExtra("name", name)
                    putExtra("phone", phone)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, serviceIntent)
                } else {
                    startService(serviceIntent)
                }

            } catch (e: JSONException) {
                Log.e("FCM", "Invalid JSON", e)
            }
        }
    }
}
