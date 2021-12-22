package com.raywenderlich.busso.server.endpoints

import com.raywenderlich.busso.server.API_VERSION
import com.raywenderlich.busso.server.model.InfoMessage
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

// Example http://localhost:8080/api/v1/findBusStop/1.0/2.0?radius=200
const val MY_LOCATION = "$API_VERSION/myLocation/{lat}/{lng}"

@KtorExperimentalLocationsAPI
@Location(MY_LOCATION)
data class MyLocationRequest(
  val lat: Float,
  val lng: Float
)

@KtorExperimentalLocationsAPI
fun Route.myLocation() {
  get<MyLocationRequest> { inputLocation ->
    call.respond(
      InfoMessage("Latitude: ${inputLocation.lat}, Longitude: ${inputLocation.lng}")
    )
  }
}