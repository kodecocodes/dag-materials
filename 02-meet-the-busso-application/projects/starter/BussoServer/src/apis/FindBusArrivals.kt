package com.raywenderlich.busso.server.endpoints

import com.raywenderlich.busso.server.API_VERSION
import com.raywenderlich.busso.server.model.BusArrivals
import com.raywenderlich.busso.server.repository.impl.RandomBusArrivalRepository
import com.raywenderlich.busso.server.repository.impl.ResourceBusStopRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

// Example http://localhost:8080/api/v1/findBusStop/1.0/2.0?radius=200
const val FIND_BUS_ARRIVALS = "$API_VERSION/findBusArrivals/{stopId}"
private val busArrivalRepository = RandomBusArrivalRepository()
private val busStopRepository = ResourceBusStopRepository()

@KtorExperimentalLocationsAPI
@Location(FIND_BUS_ARRIVALS)
data class FindBusArrivalsRequest(
    val stopId: String
)

@KtorExperimentalLocationsAPI
fun Route.findBusArrivals() {
    get<FindBusArrivalsRequest> { busStopInput ->
        val busStop = busStopRepository.findBusStopById(busStopInput.stopId)
        if (busStop == null) {
            call.respond(HttpStatusCode.NotFound, "BusStop with id ${busStopInput.stopId} not found")
        } else {
            val busArrivals = busArrivalRepository.findBusArrival(busStopInput.stopId)
            val busArrivalData = BusArrivals(busStop, busArrivals)
            call.respond(busArrivalData)
        }
    }
}
