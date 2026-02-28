package main.triptip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import main.triptip.ui.navigation.NavGraph
import main.triptip.ui.theme.TripTipTheme
import main.triptip.viewmodel.RouteViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = RouteViewModel()

        setContent {
            TripTipTheme {
                NavGraph(viewModel)
            }
        }
    }
}
