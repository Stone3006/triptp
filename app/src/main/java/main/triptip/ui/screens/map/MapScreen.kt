package main.triptip.ui.screens.map

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import main.triptip.ui.components.TripTipBottomBar
import main.triptip.utils.GeocodingUtils
import main.triptip.viewmodel.RouteViewModel
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.annotations.MarkerOptions

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(viewModel: RouteViewModel, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var destination by remember { mutableStateOf("") }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Route") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = { TripTipBottomBar(navController = navController, currentRoute = "search") }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Where to?") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val coords = GeocodingUtils.getCoordinates(destination)
                            withContext(Dispatchers.Main) {
                                coords?.let { (lat, lon) ->
                                    mapViewInstance?.getMapAsync { map ->
                                        val position = LatLng(lat, lon)
                                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 14.0))
                                        map.clear()
                                        map.addMarker(MarkerOptions().position(position).title(destination))
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            onCreate(null)
                            getMapAsync { map ->
                                // Using standard MapLibre demo tiles as a default
                                map.setStyle("https://demotiles.maplibre.org/style.json")
                                // Center on Manila by default
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(14.5995, 120.9842), 11.0))
                            }
                            mapViewInstance = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        // Lifecycle updates can be handled here if needed
                    }
                )
            }
        }
    }
}
