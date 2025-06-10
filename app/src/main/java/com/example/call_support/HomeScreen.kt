package com.example.call_support

import android.content.Context
import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.call_support.utils.AppPreferences
import com.google.accompanist.pager.*

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val CustomFontFamily = FontFamily(
    Font(R.font.poetsenoneregular, FontWeight.Normal),
    Font(R.font.poetsenoneregular, FontWeight.Bold)
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    var acceptedCallsList by remember { mutableStateOf(listOf<CallOverlayData>()) }
    val pendingCalls = remember { mutableStateListOf("Ravi Kumar", "Ali Hussain", "Sunil Yadav") }
    var isExpanded by remember { mutableStateOf(false) }

    var isOnline by remember { mutableStateOf(AppPreferences.isOnline(context)) }
    LaunchedEffect(isOnline) {
        AppPreferences.setOnline(context, isOnline)
    }

    var showOverlayManually by remember { mutableStateOf(false) }
    val manualOverlayData = CallOverlayData("Test Name", "Test Address", "9999999999")

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isExpanded) {
            WelcomeHeader(name = "Ayan")
            ImageSlider()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Toggle View"
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            items(pendingCalls) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Name Icon",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Name: ")
                                        }
                                        append(item)
                                    },
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Phone Icon",
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("Phone: ")
                                        }
                                        append("0000000000")
                                    },
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { /* TODO: Call action */ }) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            IconButton(onClick = { /* TODO: Message action */ }) {
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
                        Text(
                            "\uD83D\uDEA8 New Emergency Call",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text("\uD83D\uDC64 Name: ${manualOverlayData.name}")
                        Text("\uD83D\uDCCD Address: ${manualOverlayData.address}")
                        Text("\uD83D\uDCDE Phone: ${manualOverlayData.phone}")
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { /* TODO: Handle Call action */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", color = Color.White)
                            }

                            Button(
                                onClick = { /* TODO: Handle Message action */ },
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
fun WelcomeHeader(name: String) {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(AppPreferences.isOnline(context)) }

    // Step 1: Image Uri state
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Step 2: Launcher for Gallery picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step 3: Clickable Profile Image
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .border(2.dp, Color.White, RoundedCornerShape(50))
                        .clickable {
                            launcher.launch("image/*") // Open gallery
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = isOnline,
                    onCheckedChange = {
                        isOnline = it
                        AppPreferences.setOnline(context, it)
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
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalPager(
                count = imageList.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


