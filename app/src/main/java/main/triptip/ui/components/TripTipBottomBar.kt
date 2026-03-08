package main.triptip.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import main.triptip.R

@Composable
fun TripTipBottomBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        BottomBarItem("search", "Search", R.drawable.search_button),
        BottomBarItem("create", "Create", R.drawable.create_button),
        BottomBarItem("routes", "Saved", R.drawable.saved_button),
        BottomBarItem("profile", "Profile", R.drawable.profile_button)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}

private data class BottomBarItem(val route: String, val label: String, val iconRes: Int)
