package com.example.call_support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat.startActivity

// OverlayActivity.kt
class OverlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name") ?: "Unknown"
        val address = intent.getStringExtra("address") ?: "Unknown"
        val phone = intent.getStringExtra("phone") ?: "0000000000"

        setContent {
            MaterialTheme {
                OverlayCard(
                    data = CallOverlayData(name, address, phone),
                    onAccept = {
                        // Add to accepted calls list (we’ll handle this later)
                        finish()
                    },
                    onCall = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        startActivity(dialIntent)
                        finish()
                    },
                    onMessage = {
                        val msgIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone"))
                        startActivity(msgIntent)
                        finish()
                    },
                    onIgnore = {
                        // Add to pending calls (handle later)
                        finish()
                    }
                )
            }
        }
    }
}
