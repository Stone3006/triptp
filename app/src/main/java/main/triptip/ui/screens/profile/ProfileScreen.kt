package main.triptip.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(onNavigateToEdit: () -> Unit) {
    // Added verticalScroll to ensure all new sections are reachable on smaller screens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "My Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            ProfileAvatar()

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Calvin Velasco", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "calvin.velasco@example.com", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Edit Profile Button - Now triggers navigation
            OutlinedButton(
                onClick = onNavigateToEdit, // Updated this line
                modifier = Modifier.fillMaxWidth(0.6f),
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }

            Spacer(modifier = Modifier.height(32.dp))
            TripInsightsCard()
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Budget Settings Section - Integrated from image_dda1e2.png
        Text(text = "Commute Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileMenuOption(
                icon = Icons.Default.AccountBox,
                title = "Commute Budget",
                subtitle = "Set daily & monthly limits" // Details from image
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Account Actions
        Text(text = "Account Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileMenuOption(icon = Icons.Default.Favorite, title = "Saved Commutes")
            ProfileMenuOption(icon = Icons.Default.DateRange, title = "Trip History")
            ProfileMenuOption(icon = Icons.Default.Settings, title = "App Preferences")
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 4. Themed Logout Button - Blue accent from image_ec3500.jpg
        Button(
            onClick = { /* Logout Logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
        ) {
            Text(text = "Logout", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileAvatar() {
    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(Color(0xFFE3EDF7)) // Light blue tint to match TripTip theme
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(65.dp),
            tint = Color(0xFF007BFF)
        )
    }
}

@Composable
fun TripInsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Commuter Insights", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InsightMetricItem(label = "Trips", value = "12")
                InsightMetricItem(label = "CO2 Saved", value = "2.4kg")
                InsightMetricItem(label = "Points", value = "150")
            }
        }
    }
}

@Composable
fun InsightMetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF007BFF))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun ProfileMenuOption(icon: ImageVector, title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color(0xFFF1F6FB)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF007BFF))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
}