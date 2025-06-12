package com.example.call_support

import HomeViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.call_support.AppPreferences
import com.google.accompanist.pager.*
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.provider.Settings



val CustomFontFamily = FontFamily(
    Font(R.font.poetsenoneregular, FontWeight.Normal),
    Font(R.font.poetsenoneregular, FontWeight.Bold)
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("user_id", null) ?: return
    val isLoading by viewModel.isLoading.collectAsState()
    val name by viewModel.name.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    val pendingCalls = remember {
        mutableStateListOf("Ravi Kumar", "Ali Hussain", "Sunil Yadav", "MD Ayan Hashmi")
    }

    var isExpanded by remember { mutableStateOf(false) }
    var showOverlayManually by remember { mutableStateOf(false) }
    val manualOverlayData = CallOverlayData("Test Name", "Test Address", "9999999999")

    BackHandler { activity?.finishAffinity() }

    LaunchedEffect(userId) {
        viewModel.fetchUserData(userId, context)
    }

    if (isLoading || name.isEmpty() || isOnline == null) {
        SkeletonScreen()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!isExpanded) {
                WelcomeHeader(
                    name = name,
                    isOnline = isOnline,
                    onToggleOnline = { newValue ->
                        viewModel.updateStatus(newValue, userId, context)

                        if (newValue) {
                            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                            val packageName = context.packageName
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                                    intent.data = Uri.parse("package:$packageName")

                                    // Prevent crash if intent not supported
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Battery optimization settings not supported on this device",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }

                    },
                    userId = userId,
                    isLoading = isLoading
                )
                ImageSlider()
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (isOnline == true) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp))
                        .background(Color(0xFFECEEF8))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        items(pendingCalls) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .padding(horizontal = 30.dp, vertical = 10.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(30.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.weight(0.5f)
                                    ) {
                                        InfoRow(Icons.Default.Person, "Name: ", item)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        InfoRow(Icons.Default.Call, "Phone: ", "0000000000")
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = { }) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = "Call",
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        IconButton(onClick = { }) {
                                            Icon(
                                                imageVector = Icons.Default.Message,
                                                contentDescription = "Message",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.offline),
                        contentDescription = "Offline Image",
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }
            }
        }

        if (showOverlayManually) {
            Dialog(onDismissRequest = { showOverlayManually = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🚨 New Emergency Call", style = MaterialTheme.typography.headlineSmall)
                        Text("👤 Name: ${manualOverlayData.name}")
                        Text("📍 Address: ${manualOverlayData.address}")
                        Text("📞 Phone: ${manualOverlayData.phone}")
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", color = Color.White)
                            }

                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Default.Message, contentDescription = "Message", tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Message", color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { showOverlayManually = false },
                                modifier = Modifier.height(42.dp)
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun WelcomeHeader(
    name: String,
    isOnline: Boolean?,
    onToggleOnline: (Boolean) -> Unit,
    userId: String,
    isLoading: Boolean
) {
    val context = LocalContext.current
//    var imageUri by remember { mutableStateOf<Uri?>(null) }

//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        imageUri = uri
//    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
            )
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .border(2.dp, Color.White, RoundedCornerShape(50)),
                         contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Welcome,",
                        fontFamily = CustomFontFamily,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = name,
                        fontFamily = CustomFontFamily,
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ONLINE/OFFLINE TOGGLE OR LOADING
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    isOnline == null -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    else -> {
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { newValue ->
                                onToggleOnline(newValue)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF43A047),
                                uncheckedTrackColor = Color(0xFFE53935)
                            )
                        )
                    }
                }
            }
        }
    }
}


fun updateOnlineStatus(userId: String, isOnline: Boolean, context: Context) {
    val body = mapOf("isOnline" to isOnline)
    Log.d("UpdateStatus", "Sending request with body: $body for userId: $userId")
    RetrofitClient.apiService.updateOnlineStatus(userId, body)
        .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider() {
    val imageList = listOf(R.drawable.ambulance, R.drawable.logo, R.drawable.logonav)
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % imageList.size
            coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 14.dp, top = 12.dp, bottom = 6.dp)
            .height(180.dp)
    ) {
        HorizontalPager(
            count = imageList.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = imageList[page]),
                contentDescription = "Slider Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(20.dp)
                    )
            )
        }

        // Left Arrow
//        IconButton(
//            onClick = {
//                coroutineScope.launch {
//                    val prev = if (pagerState.currentPage == 0) imageList.lastIndex else pagerState.currentPage - 1
//                    pagerState.animateScrollToPage(prev)
//                }
//            },
//            modifier = Modifier
//                .align(Alignment.CenterStart)
//                .padding(start = 8.dp)
//                .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
//                .size(36.dp)
//        ) {
//            Icon(Icons.Default.ArrowBack, contentDescription = "Previous", tint = Color.White)
//        }

//        // Right Arrow
//        IconButton(
//            onClick = {
//                coroutineScope.launch {
//                    val next = (pagerState.currentPage + 1) % imageList.size
//                    pagerState.animateScrollToPage(next)
//                }
//            },
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .padding(end = 8.dp)
//                .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
//                .size(36.dp)
//        ) {
//            Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
//        }

        // Indicator
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(label)
                }
                append(value)
            },
            fontSize = 18.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SkeletonScreen() {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Welcome Header Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shimmer(shimmer)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Image Slider Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
                .shimmer(shimmer)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Pending Calls Card Skeletons
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            items(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmer(shimmer)
                        .background(Color.LightGray)
                )
            }
        }
    }
}


