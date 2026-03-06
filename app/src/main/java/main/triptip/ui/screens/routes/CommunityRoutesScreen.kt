package main.triptip.ui.screens.routes

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import main.triptip.viewmodel.RouteViewModel
import androidx.compose.ui.tooling.preview.Preview

data class CommunityPost(
    val id: Int,
    val authorName: String,
    val title: String,
    val subtitle: String,
    val totalPrice: Double,
    val travelTimes: List<String>,
    var upvotes: Int = 0,
    var isUpvoted: Boolean = false,
    val isVerified: Boolean = false,
    val breakdown: String? = null,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val hasThumbnail: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityRoutesScreen(viewModel: RouteViewModel) {
    var selectedFilter by remember { mutableStateOf("Nearby") }

    var isExpanded by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var subtitleInput by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") }
    var breakdownInput by remember { mutableStateOf("") }

    val posts = remember {
        mutableStateListOf(
            CommunityPost(1, "LeBron James", "Boni Ave", "Mapúa Makati", 36.0, listOf("Approx. 30-40 mins."), 152, isUpvoted = false, isVerified = true, commentCount = 12, shareCount = 6),
            CommunityPost(2, "Miguel Cruz", "España", "Taft Ave", 25.0, listOf("Approx. 45 mins."), 49, isUpvoted = false, isVerified = false, breakdown = "₱25 UV Express (Direct)", commentCount = 23, shareCount = 1),
            CommunityPost(3, "John doe", "PITX Terminal", "SM Mall of Asia", 15.0, listOf("Approx. 15 mins."), 42, isUpvoted = false, isVerified = false, breakdown = "EDSA Carousel (Gate 10)", commentCount = 5),
            CommunityPost(4, "maria gracia", "Dasmariñas", "Lawton", 95.0, listOf("Approx. 1 hr 20 mins."), 67, isUpvoted = false, isVerified = false, breakdown = "₱80 Bus + ₱15 Jeep", commentCount = 18, shareCount = 2)
        )
    }

    val displayedPosts = remember(selectedFilter, posts.toList()) {
        if (selectedFilter == "Popular") posts.sortedByDescending { it.upvotes } else posts
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 1.dp, color = Color.White) {
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    CenterAlignedTopAppBar(
                        title = { Text("Community Routes", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) },
                        navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, "Back") } },
                        actions = { IconButton(onClick = {}) { HorizontalDotsMenu() } },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                    )

                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        color = Color(0xFFF1F3F4)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Go to...", color = Color.Gray, modifier = Modifier.weight(1f), fontSize = 16.sp)
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 16.dp, end = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedFilter == "Nearby",
                            onClick = { selectedFilter = "Nearby" },
                            label = { Text("Nearby") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFE8F0FE), selectedLabelColor = Color(0xFF1967D2)),
                            shape = RoundedCornerShape(20.dp)
                        )
                        FilterChip(
                            selected = selectedFilter == "Popular",
                            onClick = { selectedFilter = "Popular" },
                            label = { Text("Popular") },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = { AppBottomNavBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().animateContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        if (!isExpanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable { isExpanded = true }
                            ) {
                                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Text("Share a commute tip...", color = Color.Gray, fontSize = 16.sp)
                            }
                        } else {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Contribute a Tip", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                    Text("Share details of your route", color = Color.Gray, fontSize = 13.sp)
                                }
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = { isExpanded = false }) {
                                    Icon(Icons.Default.Close, null, tint = Color.Gray)
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                label = { Text("Starting Point") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = subtitleInput,
                                onValueChange = { subtitleInput = it },
                                label = { Text("Destination") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = priceInput,
                                    onValueChange = { priceInput = it },
                                    label = { Text("Fare") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    prefix = { Text("₱") },
                                    singleLine = true
                                )
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = timeInput,
                                    onValueChange = { timeInput = it },
                                    label = { Text("Time") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    placeholder = { Text("30 mins") },
                                    singleLine = true
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = breakdownInput,
                                onValueChange = { breakdownInput = it },
                                label = { Text("Fare Breakdown (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text("₱13 Jeep + ₱20 Bus") }
                            )

                            Button(
                                onClick = {
                                    if (titleInput.isNotBlank() && subtitleInput.isNotBlank()) {
                                        posts.add(0, CommunityPost(
                                            id = posts.size + 1,
                                            authorName = "You",
                                            title = titleInput,
                                            subtitle = subtitleInput,
                                            totalPrice = priceInput.toDoubleOrNull() ?: 0.0,
                                            travelTimes = listOf(if(timeInput.startsWith("Approx")) timeInput else "Approx. $timeInput"),
                                            breakdown = breakdownInput.ifBlank { null },
                                            hasThumbnail = false
                                        ))
                                        titleInput = ""; subtitleInput = ""; priceInput = ""; timeInput = ""; breakdownInput = ""; isExpanded = false
                                    }
                                },
                                modifier = Modifier.align(Alignment.End).padding(top = 16.dp),
                                enabled = titleInput.isNotBlank() && subtitleInput.isNotBlank(),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("Post Tip", fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }

            items(displayedPosts, key = { it.id }) { post ->
                CommunityRouteCard(
                    post = post,
                    onUpvote = {
                        val index = posts.indexOfFirst { it.id == post.id }
                        if (index != -1) {
                            val targetPost = posts[index]
                            if (targetPost.isUpvoted) {
                                posts[index] = targetPost.copy(upvotes = targetPost.upvotes - 1, isUpvoted = false)
                            } else {
                                posts[index] = targetPost.copy(upvotes = targetPost.upvotes + 1, isUpvoted = true)
                            }
                        }
                    },
                    onReport = { }
                )
            }
        }
    }
}

@Composable
fun CommunityRouteCard(post: CommunityPost, onUpvote: () -> Unit, onReport: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF34A853)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        if (post.isVerified || post.upvotes >= 50) {
                            Spacer(Modifier.width(8.dp))
                            Surface(color = Color(0xFFE6F4EA), shape = RoundedCornerShape(4.dp)) {
                                Text("✓ Verified", color = Color(0xFF137333), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Text("${post.title} to ${post.subtitle}", color = Color.Gray, fontSize = 13.sp)
                }
                HorizontalDotsMenu()
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFAFAFA)).border(1.dp, Color(0xFFF1F1F1), RoundedCornerShape(12.dp))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                        Text("₱${post.totalPrice.toInt()} total", color = Color(0xFF8B4513), fontWeight = FontWeight.Black, fontSize = 28.sp)
                        post.breakdown?.let { Text(it, color = Color(0xFF1967D2), fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp)) }

                        post.travelTimes.forEach { time ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.Info, null, Modifier.size(14.dp), tint = Color.Gray)
                                Spacer(Modifier.width(4.dp))
                                Text(time, fontSize = 14.sp, color = Color.DarkGray)
                            }
                        }
                    }
                    if (post.hasThumbnail) {
                        Box(modifier = Modifier.size(height = 120.dp, width = 140.dp).background(Color(0xFFE0E0E0)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Place, null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                UserAvatarGroup()
                Spacer(Modifier.width(16.dp))
                MetricItem(icon = Icons.Default.Email, count = post.commentCount)
                Spacer(Modifier.weight(1f))
                val activeBlue = Color(0xFF1967D2)
                val upvoteColor = if (post.isUpvoted) activeBlue else Color.Gray
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onUpvote, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.KeyboardArrowUp, null, tint = upvoteColor, modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("${post.upvotes}", fontSize = 15.sp, color = upvoteColor, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                }
                Text("${post.shareCount}", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun HorizontalDotsMenu() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        repeat(3) {
            Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.Gray))
            if (it < 2) Spacer(Modifier.width(3.dp))
        }
    }
}

@Composable
fun UserAvatarGroup() {
    Box(contentAlignment = Alignment.CenterStart) {
        repeat(2) { index ->
            Surface(
                modifier = Modifier.padding(start = (index * 18).dp).size(32.dp),
                shape = CircleShape, border = BorderStroke(2.dp, Color.White), color = if(index == 0) Color.LightGray else Color(0xFF34A853)
            ) { Box(contentAlignment = Alignment.Center) { Text(if(index==0) "M" else "E", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
        }
    }
}

@Composable
fun MetricItem(icon: androidx.compose.ui.graphics.vector.ImageVector, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), tint = Color.Gray)
        Text(" $count", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AppBottomNavBar() {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Search, null, tint = Color(0xFF1967D2)) }, label = { Text("Search") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Refresh, null) }, label = { Text("Create") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.List, null) }, label = { Text("Saved") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CommunityRoutesScreenPreview() {
    val viewModel = RouteViewModel()
    CommunityRoutesScreen(viewModel = viewModel)
}