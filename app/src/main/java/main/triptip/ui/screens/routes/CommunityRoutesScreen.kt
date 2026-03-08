package main.triptip.ui.screens.routes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import main.triptip.viewmodel.RouteViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import main.triptip.R


// Local Data Model
data class CommunityPost(
    val id: Int,
    val title: String,
    val subtitle: String,
    val totalPrice: Double,
    val travelTimes: List<String>,
    val upvotes: Int = 0,
    val isVerified: Boolean = false,
    val breakdown: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityRoutesScreen(viewModel: RouteViewModel, navController: NavController) {
    val characterLimit = 500
    var newPostText by remember { mutableStateOf("") }

    // Finalized Realistic Commute Data
    val posts = remember {
        mutableStateListOf(
            // 1. Mandaluyong to Mapua Makati
            CommunityPost(
                id = 1,
                title = "Mandaluyong (Boni)",
                subtitle = "to Mapúa University Makati",
                totalPrice = 36.0,
                travelTimes = listOf("Approx. 30 mins.", "Approx. 45 mins."),
                upvotes = 152,
                isVerified = true,
                breakdown = "₱13 Jeepney + ₱23 MRT-3/Jeep"
            ),
            // 2. Anywhere within Metro Manila (España to Taft)
            CommunityPost(
                id = 2,
                title = "España (UST/P. Noval)",
                subtitle = "to Taft Ave (DLSU/Vito Cruz)",
                totalPrice = 25.0,
                travelTimes = listOf("Approx. 35 mins.", "Approx. 50 mins."),
                upvotes = 94,
                isVerified = true,
                breakdown = "₱25 UV Express (Direct)"
            ),
            // 3. PITX to Anywhere Nearby (MOA)
            CommunityPost(
                id = 3,
                title = "PITX Terminal",
                subtitle = "to SM Mall of Asia (MOA)",
                totalPrice = 15.0,
                travelTimes = listOf("Approx. 15 mins."),
                upvotes = 42,
                isVerified = false,
                breakdown = "EDSA Carousel (Gate 10)"
            ),
            // 4. Cavite to Metro Manila (Dasma to Lawton)
            CommunityPost(
                id = 4,
                title = "Dasmariñas, Cavite",
                subtitle = "to Lawton/Post Office (Manila)",
                totalPrice = 95.0,
                travelTimes = listOf("Approx. 1 hr 20 mins.", "Approx. 2 hrs (Peak)"),
                upvotes = 67,
                isVerified = false,
                breakdown = "₱80 Bus (Aguinaldo Hwy) + ₱15 Jeep"
            )
        )
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 3.dp, color = Color.White) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    CenterAlignedTopAppBar(
                        title = { Text("Community Routes", fontWeight = FontWeight.Black, fontSize = 20.sp) },
                        navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, "Back") } },
                        actions = { IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, "More") } }
                    )

                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        color = Color(0xFFF1F3F4)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, null, tint = Color.Gray)
                            Spacer(Modifier.width(12.dp))
                            Text("Go to...", color = Color.Gray, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = true,
                            onClick = {},
                            label = { Text("Nearby") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, null, Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFE8F0FE))
                        )
                        FilterChip(selected = false, onClick = {}, label = { Text("Popular") })
                    }
                }
            }
        },
        bottomBar = { MockBottomNavBar(navController, "routes") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = newPostText,
                            onValueChange = { if (it.length <= characterLimit) newPostText = it },
                            placeholder = { Text("Share a commute tip...") },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                Text("${newPostText.length} / $characterLimit", color = if (newPostText.length >= characterLimit) Color.Red else Color.Unspecified)
                            }
                        )
                        Button(
                            onClick = {
                                if (newPostText.isNotBlank()) {
                                    posts.add(0, CommunityPost(posts.size + 1, "New Tip", newPostText, 0.0, emptyList()))
                                    newPostText = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
                        ) {
                            Text("Post Tip")
                        }
                    }
                }
            }

            items(posts) { post ->
                CommunityRouteCard(post)
            }
        }
    }
}

@Composable
fun CommunityRouteCard(post: CommunityPost) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF34A853)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Place, null, tint = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        if (post.isVerified || post.upvotes >= 50) {
                            Spacer(Modifier.width(6.dp))
                            Surface(color = Color(0xFFE6F4EA), shape = RoundedCornerShape(4.dp)) {
                                Text("✓ Verified", color = Color(0xFF137333), fontSize = 10.sp, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Text(post.subtitle, color = Color.Gray, fontSize = 13.sp)
                }
                Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, Color(0xFFF1F1F1), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("₱${post.totalPrice.toInt()} total", color = Color(0xFF8B4513), fontWeight = FontWeight.Black, fontSize = 24.sp)

                    post.breakdown?.let {
                        Text(text = it, color = Color(0xFF1967D2), fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(vertical = 2.dp))
                    }

                    post.travelTimes.forEach { time ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                            Icon(Icons.Default.Info, null, Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(Modifier.width(4.dp))
                            Text(time, fontSize = 13.sp, color = Color.DarkGray)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.CenterStart) {
                    repeat(3) { index ->
                        Surface(modifier = Modifier.padding(start = (index * 16).dp).size(24.dp), shape = CircleShape, border = BorderStroke(2.dp, Color.White), color = Color.LightGray) {}
                    }
                }
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Default.Email, null, Modifier.size(18.dp), tint = Color.Gray)
                Text(" 12", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Default.Send, null, Modifier.size(18.dp), tint = Color.Gray)
                Text(" 23", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.Share, null, Modifier.size(18.dp), tint = Color.Gray)
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Default.List, null, Modifier.size(18.dp), tint = Color.Gray)
            }
        }
    }
}

@Composable
fun MockBottomNavBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        Triple("search", "Search", R.drawable.search_button),
        Triple("create", "Create", R.drawable.create_button),
        Triple("routes", "Saved", R.drawable.saved_button),
        Triple("profile", "Profile", R.drawable.profile_button)
    )
    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.first == currentRoute,
                onClick = {
                    navController.navigate(item.first) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Image(painter = painterResource(item.third), contentDescription = item.second, modifier = Modifier.size(22.dp)) },
                label = { Text(item.second) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CommunityRoutesScreenPreview() {
    val viewModel = RouteViewModel()

    CommunityRoutesScreen(viewModel = viewModel, navController = rememberNavController())
}
