package com.raywenderlich.busso.server.repository.impl

import com.raywenderlich.busso.server.model.BusArrival
import com.raywenderlich.busso.server.model.BusArrivalGroup
import com.raywenderlich.busso.server.repository.BusArrivalRepository
import java.util.*
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

/**
 * Number of arrivals for line
 */
fun arrivalNumberRange() = 0..nextInt(3, 10)
fun arrivalGroupRange() = 0..nextInt(1, 4)
private val busStopRepository = ResourceBusStopRepository()

/**
 * Implementation for the BusArrivalRepository which returns random values
 */
class RandomBusArrivalRepository : BusArrivalRepository {
  override suspend fun findBusArrival(busStopId: String): List<BusArrivalGroup> {
    val busStop = busStopRepository.findBusStopById(busStopId)
    if (busStop == null) {
      return emptyList()
    }
    return mutableListOf<BusArrivalGroup>().apply {
      arrivalGroupRange().forEach {
        add(
          BusArrivalGroup(
            lineId = "1",
            lineName = lines.random(),
            destination = destinations.random(),
            arrivals = generateRandomBusArrival()
          )
        )
      }
    }
  }

}


private fun generateRandomBusArrival(): List<BusArrival> = mutableListOf<BusArrival>()
  .run {
    arrivalNumberRange().forEach {
      add(
        BusArrival(
          id = "$it",
          vehicleId = "vehicle_$it",
          lineId = "line $it",
          lineName = "line $it",
          destinationName = "Russel Square",
          expectedArrival = randomTime()
        )
      )
    }
    sortedBy { it.expectedArrival }
  }

private val lines = listOf<String>("Piccadilly Line", "Jubilee", "District Line")
private val destinations =
  listOf<String>("Wimbledon", "Piccadilly", "Warren Street", "Trafalgar Square")

fun <A> List<A>.random(): A = get(nextInt(0, size))
private val MAX_TIME_MILLIS = 1000 * 60 * 60L // 1 hour
fun randomTime() = Date(Date().time + nextLong(0, MAX_TIME_MILLIS))