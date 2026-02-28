package main.triptip.data.model

data class Route(
    val id: String,
    val start: String,
    val destination: String,
    val totalFare: Double,
    val duration: String,
    val imageUrl: String = ""
)