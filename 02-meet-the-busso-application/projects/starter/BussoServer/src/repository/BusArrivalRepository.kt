package com.raywenderlich.busso.server.repository

import com.raywenderlich.busso.server.model.BusArrivalGroup

/**
 * Abstraction for the Repository containing arrival times for a given Stop
 */
interface BusArrivalRepository {

    /**
     * Get all the arrivals for the given BusStopId
     */
    suspend fun findBusArrival(busStopId: String): List<BusArrivalGroup>
}