package main.triptip.data.model

data class Route(
    val id: String,
    val start: String,
    val destination: String,
    val totalFare: Double,
    val duration: String,
    val imageUrl: String = "",
    val steps: List<RouteStep> = emptyList()
)

data class RouteStep(
    val transportName: String,
    val fare: Double,
    val details: String,
    val photoUri: String = ""
)
