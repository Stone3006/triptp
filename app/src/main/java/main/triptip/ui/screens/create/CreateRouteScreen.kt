package main.triptip.ui.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import main.triptip.R
import main.triptip.data.model.RouteStep
import main.triptip.viewmodel.RouteViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val BluePrimary = Color(0xFF1E88E5)
private val DarkBookmarkBlue = Color(0xFF0D47A1)
private val GreenPrimary = Color(0xFF6CC04A)
private val CardBackground = Color(0xFFF7F7F7)
private val AccentOrange = Color(0xFFF4A825)
private val SoftBorder = Color(0xFFE8E8E8)
private val SuggestionLocations = listOf(
    "Marikina",
    "UP Diliman",
    "Manila",
    "Mandaluyong",
    "SM Dasmariñas",
    "Imus Plaza",
    "Cubao",
    "Makati",
    "Ortigas",
    "Quezon City",
    "Pasig",
    "Taguig"
)

private val SampleSearchResults = listOf(
    RouteStep(
        transportName = "Jitney",
        fare = 12.0,
        details = "Gateway Mall - Bookstore"
    ),
    RouteStep(
        transportName = "UV Express",
        fare = 24.0,
        details = "UP Ikot Tricycle - Bookstore"
    ),
    RouteStep(
        transportName = "Jeepney",
        fare = 8.0,
        details = "Katipunan Flyover - UP Gate"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRouteScreen(navController: NavController, viewModel: RouteViewModel) {
    var origin by rememberSaveable { mutableStateOf("") }
    var destination by rememberSaveable { mutableStateOf("") }
    var showOriginSuggestions by remember { mutableStateOf(false) }
    var showDestinationSuggestions by remember { mutableStateOf(false) }
    var originError by remember { mutableStateOf<String?>(null) }
    var destinationError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var selectedBudget by rememberSaveable { mutableStateOf(50) }
    var includeTricycle by rememberSaveable { mutableStateOf(true) }
    var includeJeepney by rememberSaveable { mutableStateOf(true) }
    var includeUv by rememberSaveable { mutableStateOf(true) }
    var searchAttempted by remember { mutableStateOf(false) }
    var showPremadeRoutes by rememberSaveable { mutableStateOf(false) }
    val recentSearches = remember { mutableStateListOf<Pair<String, String>>() }
    val addedSteps = remember { mutableStateListOf<RouteStep>() }
    var showAddStepDialog by remember { mutableStateOf(false) }
    var showSaveMessage by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var bookmarked by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val availableSuggestions = remember(origin, destination, selectedBudget, includeTricycle, includeJeepney, includeUv) {
        SampleSearchResults.filter { step ->
            val transport = step.transportName.lowercase()
            val budgetOk = step.fare <= selectedBudget
            val transportOk = when {
                transport.contains("tric") -> includeTricycle
                transport.contains("uv") -> includeUv
                else -> includeJeepney
            }
            budgetOk && transportOk
        }
    }

    fun validateInputs(forSearch: Boolean = true): Boolean {
        originError = null
        destinationError = null
        generalError = null

        val cleanOrigin = origin.trim()
        val cleanDestination = destination.trim()

        if (cleanOrigin.isBlank()) originError = "Origin is required."
        else if (cleanOrigin.length > 100) originError = "Origin is too long."

        if (cleanDestination.isBlank()) destinationError = "Destination is required."
        else if (cleanDestination.length > 100) destinationError = "Destination is too long."

        if (cleanOrigin.isNotBlank() && cleanDestination.isNotBlank() && cleanOrigin.equals(cleanDestination, ignoreCase = true)) {
            generalError = "Origin and Destination cannot be the same."
        }

        if (forSearch && selectedBudget < 8) {
            generalError = "No routes found within your current budget."
        }

        return originError == null && destinationError == null && generalError == null
    }

    fun performSearch() {
        searchAttempted = true
        if (!validateInputs()) return
        val searchPair = origin.trim() to destination.trim()
        if (searchPair !in recentSearches) recentSearches.add(0, searchPair)
        if (recentSearches.size > 5) recentSearches.removeLast()
        showPremadeRoutes = true
        if (availableSuggestions.isEmpty()) {
            generalError = "No premade routes match your filters right now, but you can still build your own route below."
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create Route", fontWeight = FontWeight.SemiBold) },
                actions = {
                    val bookmarkTint by animateColorAsState(
                        targetValue = if (bookmarked) DarkBookmarkBlue else Color(0xFF8A8A8A),
                        animationSpec = tween(220), label = "bookmarkTint"
                    )
                    IconButton(onClick = {
                        bookmarked = !bookmarked
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                if (bookmarked) "Route bookmarked" else "Bookmark removed"
                            )
                        }
                    }) {
                        Icon(
                            imageVector = if (bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (bookmarked) "Bookmarked route draft" else "Bookmark route draft",
                            tint = bookmarkTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        },
        bottomBar = { TripTipBottomBar(currentRoute = "create", navController = navController) },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(4.dp))
                RouteLocationField(
                    label = "Origin",
                    value = origin,
                    onValueChange = {
                        if (it.length <= 100) {
                            origin = it
                            originError = null
                            generalError = null
                            showOriginSuggestions = false
                        }
                    },
                    placeholder = "Marikina",
                    error = originError,
                    showSuggestions = showOriginSuggestions,
                    suggestions = filteredSuggestions(origin, recentSearches.map { it.first }),
                    onSuggestionClick = {
                        origin = it
                        showOriginSuggestions = false
                        originError = null
                    },
                    onClear = {
                        origin = ""
                        originError = null
                        showOriginSuggestions = false
                    },
                    gpsEnabled = true,
                    onUseCurrentLocation = {
                        origin = "Current Location, Marikina"
                        originError = null
                        generalError = null
                        showOriginSuggestions = false
                    },
                    onToggleSuggestions = {
                        showOriginSuggestions = !showOriginSuggestions
                    }
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 2.dp,
                        color = Color.White,
                        modifier = Modifier.clickable {
                            val temp = origin
                            origin = destination
                            destination = temp
                            originError = null
                            destinationError = null
                            generalError = null
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap",
                            tint = BluePrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                RouteLocationField(
                    label = "Destination",
                    value = destination,
                    onValueChange = {
                        if (it.length <= 100) {
                            destination = it
                            destinationError = null
                            generalError = null
                            showDestinationSuggestions = false
                        }
                    },
                    placeholder = "UP Diliman",
                    error = destinationError,
                    showSuggestions = showDestinationSuggestions,
                    suggestions = filteredSuggestions(destination, recentSearches.map { it.second }),
                    onSuggestionClick = {
                        destination = it
                        showDestinationSuggestions = false
                        destinationError = null
                    },
                    onClear = {
                        destination = ""
                        destinationError = null
                        showDestinationSuggestions = false
                    },
                    gpsEnabled = false,
                    onUseCurrentLocation = { },
                    onToggleSuggestions = {
                        showDestinationSuggestions = !showDestinationSuggestions
                    }
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { performSearch() },
                        enabled = origin.isNotBlank() && destination.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text("Find Premade Routes")
                    }
                    Button(
                        onClick = { showFilterDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F7FE), contentColor = BluePrimary)
                    ) {
                        Text("Filters")
                    }
                }
            }


            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF6FAFF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SoftBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Build your own route", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Type any origin and destination you want. Premade routes are optional.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            TextButton(onClick = { showPremadeRoutes = !showPremadeRoutes }) {
                                Text(if (showPremadeRoutes) "Hide premade" else "Show premade")
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (showPremadeRoutes) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (generalError != null) {
                item {
                    Text(generalError.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (showPremadeRoutes && availableSuggestions.isNotEmpty()) {
                item {
                    Text(
                        "Premade route ideas (optional)",
                        fontWeight = FontWeight.SemiBold,
                        color = BluePrimary
                    )
                }
                itemsIndexed(availableSuggestions) { index, step ->
                    SearchResultCard(
                        step = step,
                        index = index,
                        onAdd = {
                            if (addedSteps.none { existing ->
                                    existing.transportName == step.transportName && existing.details == step.details
                                }
                            ) {
                                addedSteps.add(step)
                            }
                        }
                    )
                }
            }

            if (addedSteps.isNotEmpty()) {
                itemsIndexed(addedSteps) { index, step ->
                    StyledStepCard(
                        step = step,
                        index = index,
                        onRemove = { addedSteps.remove(step) }
                    )
                }
            }

            item {
                FareSummaryCard(totalFare = addedSteps.sumOf { it.fare })
            }

            item {
                Button(
                    onClick = { showAddStepDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Add Step")
                }
            }

            item {
                Button(
                    onClick = {
                        if (validateInputs(forSearch = false) && addedSteps.isNotEmpty()) {
                            viewModel.addRoute(
                                start = origin.trim(),
                                destination = destination.trim(),
                                fare = addedSteps.sumOf { it.fare },
                                duration = "Custom route",
                                imageUrl = addedSteps.firstOrNull { it.photoUri.isNotBlank() }?.photoUri.orEmpty(),
                                steps = addedSteps.toList()
                            )
                            showSaveMessage = true
                        } else if (addedSteps.isEmpty()) {
                            generalError = "Add at least one step before saving."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Save Route")
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            selectedBudget = selectedBudget,
            includeTricycle = includeTricycle,
            includeJeepney = includeJeepney,
            includeUv = includeUv,
            onDismiss = { showFilterDialog = false },
            onApply = { budget, tri, jeep, uv ->
                selectedBudget = budget
                includeTricycle = tri
                includeJeepney = jeep
                includeUv = uv
                showFilterDialog = false
                generalError = null
            }
        )
    }

    if (showAddStepDialog) {
        AddStepDialog(
            onDismiss = { showAddStepDialog = false },
            onAdd = { step ->
                addedSteps.add(step)
                showAddStepDialog = false
            }
        )
    }

    if (showSaveMessage) {
        AlertDialog(
            onDismissRequest = { showSaveMessage = false },
            confirmButton = {
                TextButton(onClick = { showSaveMessage = false }) { Text("OK") }
            },
            title = { Text("Route saved") },
            text = { Text("Your route was saved successfully.") }
        )
    }
}

@Composable
private fun RouteLocationField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    showSuggestions: Boolean,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    onClear: () -> Unit,
    gpsEnabled: Boolean,
    onUseCurrentLocation: () -> Unit,
    onToggleSuggestions: (() -> Unit)? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(Icons.Default.Place, contentDescription = null, tint = if (label == "Origin") GreenPrimary else AccentOrange)
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (gpsEnabled) {
                        IconButton(onClick = onUseCurrentLocation) {
                            Icon(Icons.Default.GpsFixed, contentDescription = "My Location", tint = BluePrimary)
                        }
                    }
                    if (onToggleSuggestions != null) {
                        IconButton(onClick = onToggleSuggestions) {
                            Icon(Icons.Default.ExpandMore, contentDescription = "Show suggestions", tint = BluePrimary)
                        }
                    }
                    if (value.isNotBlank()) {
                        IconButton(onClick = onClear) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            },
            isError = error != null,
            shape = RoundedCornerShape(14.dp)
        )
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        DropdownMenu(
            expanded = showSuggestions && suggestions.isNotEmpty(),
            onDismissRequest = { onToggleSuggestions?.invoke() }
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = { onSuggestionClick(suggestion) }
                )
            }
        }
    }
}

@Composable
private fun SearchResultCard(step: RouteStep, index: Int, onAdd: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.border(1.dp, SoftBorder, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("₱${step.fare.roundToInt()}", color = AccentOrange, fontWeight = FontWeight.Bold)
                }
                Text(step.transportName, fontWeight = FontWeight.SemiBold)
                Text(step.details, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Button(
                    onClick = onAdd,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if (index < 2) "Add Photo" else "Add Step")
                }
            }
            Box(
                modifier = Modifier
                    .size(width = 128.dp, height = 96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEAEAEA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
private fun StyledStepCard(step: RouteStep, index: Int, onRemove: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.border(1.dp, SoftBorder, RoundedCornerShape(22.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Route, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("₱${step.fare.roundToInt()}", color = AccentOrange, fontWeight = FontWeight.Bold)
                }
                Text(step.transportName, fontWeight = FontWeight.SemiBold)
                Text(step.details, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = BluePrimary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (step.photoUri.isBlank()) "Add Photo" else "Photo Added", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    TextButton(onClick = onRemove) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Remove")
                    }
                }
            }

            if (step.photoUri.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(step.photoUri).crossfade(true).build(),
                    contentDescription = "Step photo",
                    modifier = Modifier
                        .size(width = 128.dp, height = 96.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 128.dp, height = 96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEAEAEA)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray)
                        Text("No Photo", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun FareSummaryCard(totalFare: Double) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF8E9)),
        modifier = Modifier.animateContentSize()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("₱${DecimalFormat("0.##").format(totalFare)} Total Fare", color = AccentOrange, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AddStepDialog(onDismiss: () -> Unit, onAdd: (RouteStep) -> Unit) {
    var transportName by rememberSaveable { mutableStateOf("") }
    var fareText by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }
    var selectedPhotoUri by rememberSaveable { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedPhotoUri = uri?.toString().orEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val fare = fareText.toDoubleOrNull()
                    when {
                        transportName.isBlank() -> localError = "Transport name is required."
                        fare == null || fare < 0 -> localError = "Enter a valid fare."
                        details.isBlank() -> localError = "Step details are required."
                        else -> onAdd(RouteStep(transportName.trim(), fare, details.trim(), selectedPhotoUri))
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Step") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Fill in the transport, fare, details, and optional photo for this step.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                OutlinedTextField(value = transportName, onValueChange = { transportName = it }, label = { Text("Transport Name") }, singleLine = true)
                OutlinedTextField(
                    value = fareText,
                    onValueChange = { fareText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Fare") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(value = details, onValueChange = { details = it }, label = { Text("Details") })
                Button(onClick = { picker.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (selectedPhotoUri.isBlank()) "Add Photo" else "Change Photo")
                }
                if (selectedPhotoUri.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(selectedPhotoUri).crossfade(true).build(),
                        contentDescription = "Selected Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                if (localError != null) {
                    Text(localError.orEmpty(), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
private fun FilterDialog(
    selectedBudget: Int,
    includeTricycle: Boolean,
    includeJeepney: Boolean,
    includeUv: Boolean,
    onDismiss: () -> Unit,
    onApply: (Int, Boolean, Boolean, Boolean) -> Unit
) {
    var budget by rememberSaveable { mutableStateOf(selectedBudget.toString()) }
    var tri by rememberSaveable { mutableStateOf(includeTricycle) }
    var jeep by rememberSaveable { mutableStateOf(includeJeepney) }
    var uv by rememberSaveable { mutableStateOf(includeUv) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onApply(budget.toIntOrNull() ?: selectedBudget, tri, jeep, uv) }) { Text("Apply") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Filters") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it.filter(Char::isDigit) },
                    label = { Text("Maximum Budget") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                TransportCheckboxRow("Tricycle", tri) { tri = it }
                TransportCheckboxRow("Jeepney", jeep) { jeep = it }
                TransportCheckboxRow("UV Express", uv) { uv = it }
            }
        }
    )
}

@Composable
private fun TransportCheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}

@Composable
private fun TripTipBottomBar(currentRoute: String, navController: NavController) {
    val items = listOf(
        BottomBarItem("search", "Search", R.drawable.search_button),
        BottomBarItem("create", "Create", R.drawable.create_button),
        BottomBarItem("routes", "Saved", R.drawable.saved_button),
        BottomBarItem("profile", "Profile", R.drawable.profile_button)
    )

    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val isSelected = item.route == currentRoute
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(item.label, color = if (isSelected) BluePrimary else Color.Gray, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

private data class BottomBarItem(val route: String, val label: String, val iconRes: Int)

private fun filteredSuggestions(currentValue: String, recent: List<String>): List<String> {
    val source = (recent + SuggestionLocations).distinct()
    val query = currentValue.trim()
    return if (query.isBlank()) source.take(5) else source.filter { it.contains(query, ignoreCase = true) }.take(5)
}
