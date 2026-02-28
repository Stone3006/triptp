package main.triptip.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import main.triptip.data.model.Route
import main.triptip.data.repository.RouteRepository

class RouteViewModel : ViewModel() {

    private val _routes = MutableStateFlow(RouteRepository.getRoutes())
    val routes: StateFlow<List<Route>> = _routes

    fun addRoute(
        start: String,
        destination: String,
        fare: Double,
        duration: String,
        imageUrl: String
    ) {
        RouteRepository.addRoute(start, destination, fare, duration, imageUrl)
        _routes.value = RouteRepository.getRoutes()
    }
}