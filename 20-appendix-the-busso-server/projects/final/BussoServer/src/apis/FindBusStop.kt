package com.raywenderlich.busso.server.endpoints

import com.raywenderlich.busso.server.API_VERSION
import com.raywenderlich.busso.server.repository.BusStopRepository
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

// Example http://localhost:8080/api/v1/findBusStop/1.0/2.0?radius=200
const val FIND_BUS_STOP = "$API_VERSION/findBusStop/{lat}/{lng}"

@KtorExperimentalLocationsAPI
@Location(FIND_BUS_STOP)
data class FindBusStopRequest(
  val lat: Float,
  val lng: Float
)

@KtorExperimentalLocationsAPI
fun Route.findBusStop() {

  val busStopRepository: BusStopRepository by inject()

  get<FindBusStopRequest> { inputLocation ->
    // If there's a radius we add it as distance
    val radius = call.parameters.get("radius")?.toInt() ?: 0
    call.respond(
      busStopRepository.findBusStopByLocation(
        inputLocation.lat,
        inputLocation.lng,
        radius
      )
    )
  }
}