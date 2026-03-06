package main.triptip.data.repository

import main.triptip.data.model.Route
import main.triptip.data.model.RouteStep
import java.util.UUID

object RouteRepository {

    private val routes = mutableListOf<Route>()

    fun getRoutes(): List<Route> = routes

    fun addRoute(
        start: String,
        destination: String,
        fare: Double,
        duration: String,
        imageUrl: String,
        steps: List<RouteStep> = emptyList()
    ) {
        routes.add(
            Route(
                id = UUID.randomUUID().toString(),
                start = start,
                destination = destination,
                totalFare = fare,
                duration = duration,
                imageUrl = imageUrl,
                steps = steps
            )
        )
    }
}
