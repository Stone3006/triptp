package main.triptip.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import main.triptip.ui.screens.create.CreateRouteScreen
import main.triptip.ui.screens.routes.CommunityRoutesScreen
import main.triptip.ui.screens.search.SearchScreen
import main.triptip.viewmodel.RouteViewModel

@Composable
fun NavGraph(viewModel: RouteViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "routes") {
        composable("search") { SearchScreen(navController) }
        composable("create") { CreateRouteScreen(navController, viewModel) }
        composable("routes") { CommunityRoutesScreen(viewModel, navController) }
    }
}
