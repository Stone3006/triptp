package main.triptip.ui.screens.map

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import main.triptip.utils.GeocodingUtils
import main.triptip.viewmodel.RouteViewModel
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(viewModel: RouteViewModel) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var destination by remember { mutableStateOf("") }

    Column {

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination in Manila") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {

                    val destCoords =
                        GeocodingUtils.getCoordinates(destination)

                    // Use OSRM routing here
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Find Route")
        }

        AndroidView(
            factory = {
                val mapView = MapView(it)
                mapView.layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                mapView.getMapAsync { map ->
                    map.setStyle("https://demotiles.maplibre.org/style.json")
                }

                mapView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}