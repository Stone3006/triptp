package main.triptip.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import main.triptip.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Search") }) },
        bottomBar = { SearchBottomBar("search", navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Search is not finished yet.", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.padding(8.dp))
            Text("Use Create Route to add routes or Community Routes to view posts.")
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = { navController.navigate("create") }) { Text("Go to Create Route") }
        }
    }
}

@Composable
private fun SearchBottomBar(currentRoute: String, navController: NavController) {
    val items = listOf(
        Triple("search", "Search", R.drawable.search_button),
        Triple("create", "Create", R.drawable.create_button),
        Triple("routes", "Saved", R.drawable.saved_button),
        Triple("profile", "Profile", R.drawable.profile_button)
    )
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val isSelected = item.first == currentRoute
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable {
                    navController.navigate(item.first) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }) {
                    Image(painter = painterResource(item.third), contentDescription = item.second, modifier = Modifier.size(22.dp))
                    Text(item.second, color = if (isSelected) Color(0xFF1E88E5) else Color.Gray, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
