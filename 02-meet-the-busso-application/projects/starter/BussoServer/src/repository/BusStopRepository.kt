package com.raywenderlich.busso.server.repository

import com.raywenderlich.busso.server.model.BusStop

/**
 * The Repository for the BusStop
 */
interface BusStopRepository {

    /**
     * Returns a List of BusStop with a distance from the given location at most radius
     */
    suspend fun findBusStopByLocation(latitude: Float, longitude: Float, radius: Int): List<BusStop>

    /**
     * Find a BusStop by its id
     */
    suspend fun findBusStopById(budStopId: String): BusStop?
}