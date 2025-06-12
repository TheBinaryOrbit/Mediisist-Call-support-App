package com.example.call_support

import androidx.compose.foundation.shape.RoundedCornerShape

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog


class OverlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name") ?: "Unknown"
        val phone = intent.getStringExtra("phoneNumber") ?: "N/A"

        setContent {
            MaterialTheme {
                Dialog(onDismissRequest = { finish() }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("🚑 Emergency Request", style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("👤 Name: $name")
                            Text("📞 Phone: $phone")
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(onClick = { finish() }) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }
        }
    }
}
