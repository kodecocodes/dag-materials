package com.raywenderlich.busso.server.model

import java.util.*

/**
 * This encapsulates all the data about arrivals for a BusStop
 */
data class BusArrivals(
  val busStop: BusStop,
  val arrivalGroups: List<BusArrivalGroup>
)

/**
 * This contains the information for the group about a single line
 */
data class BusArrivalGroup(
  val lineId: String,
  val lineName: String,
  val destination: String,
  val arrivals: List<BusArrival>
)

/**
 * This is the model for the Arrivals for a line at a BusStop
 */
data class BusArrival(
  val id: String,
  val vehicleId: String?,
  val lineId: String,
  val lineName: String,
  val destinationName: String,
  val expectedArrival: Date
)