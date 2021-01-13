package com.raywenderlich.busso.server.endpoints

import com.raywenderlich.busso.server.API_VERSION
import com.raywenderlich.busso.server.model.InfoMessage
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.random.Random

// Example http://localhost:8080/api/v1/findBusStop/1.0/2.0?radius=200
const val LOCATION_WEATHER = "$API_VERSION/weather/{lat}/{lng}"
val WEATHER =
  listOf("Sunny", "Foggy", "Windy", "Stormy", "Rainy", "Drizzly", "Snowy", "Hot", "Warm")

@KtorExperimentalLocationsAPI
@Location(LOCATION_WEATHER)
data class LocationWeatherRequest(
  val lat: Float,
  val lng: Float
)

@KtorExperimentalLocationsAPI
fun Route.weather() {
  get<LocationWeatherRequest> { inputLocation ->
    val randomWeather = WEATHER[Random.nextInt(0, WEATHER.size)]
    call.respond(
      InfoMessage("Today is: $randomWeather")
    )
  }
}