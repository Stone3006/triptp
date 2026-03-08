package main.triptip.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import main.triptip.ui.screens.authentication.LoginScreen
import main.triptip.ui.screens.authentication.RegisterScreen
import main.triptip.ui.screens.create.CreateRouteScreen
import main.triptip.ui.screens.map.MapScreen
import main.triptip.ui.screens.profile.EditProfileScreen
import main.triptip.ui.screens.profile.ProfileScreen
import main.triptip.ui.screens.routes.CommunityRoutesScreen
import main.triptip.ui.screens.search.SearchScreen
import main.triptip.viewmodel.RouteViewModel

@Composable
fun NavGraph(viewModel: RouteViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = { 
                    navController.navigate("routes") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable("search") { MapScreen(viewModel, navController) }
        composable("create") { CreateRouteScreen(navController, viewModel) }
        composable("routes") { CommunityRoutesScreen(viewModel, navController) }
        composable("profile") { 
            ProfileScreen(
                navController = navController,
                onNavigateToEdit = { navController.navigate("edit_profile") }
            )
        }
        composable("edit_profile") { 
            EditProfileScreen(onBack = { navController.popBackStack() }) 
        }
    }
}
