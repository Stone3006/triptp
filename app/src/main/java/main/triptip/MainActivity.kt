package main.triptip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import main.triptip.ui.navigation.NavGraph
import main.triptip.ui.screens.authentication.LoginScreen
import main.triptip.ui.screens.authentication.RegisterScreen
import main.triptip.ui.screens.profile.EditProfileScreen
import main.triptip.ui.screens.profile.ProfileScreen
import main.triptip.ui.theme.TripTipTheme
import main.triptip.viewmodel.RouteViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = RouteViewModel()

        setContent {
            // if you want to test the login screen and register screen, unhide this
           /* val currentScreen = remember { mutableStateOf("login") }

            when (currentScreen.value) {
                "login" -> {
                    LoginScreen(
                        onNavigateToRegister = {
                            // This is the "wire" that triggers the switch
                            currentScreen.value = "register"
                        },
                        onLoginSuccess = {
                            currentScreen.value = "profile"
                        }
                    )
                }
                "register" -> {
                    RegisterScreen(
                        onNavigateToLogin = {
                            currentScreen.value = "login"
                        }
                    )
                }
                "profile" -> {
                    ProfileScreen()
                }
            }
            */
            val currentScreen = remember { mutableStateOf("profile") } // Change this to "profile"

            when (currentScreen.value) {
                "profile" -> {
                    ProfileScreen(
                        onNavigateToEdit = { currentScreen.value = "edit_profile" }
                    )
                }
                "edit_profile" -> {
                    EditProfileScreen(
                        onBack = { currentScreen.value = "profile" }
                    )
                }
            }

        }
    }
}
