package com.raywenderlich.busso.server.repository.impl

import com.google.gson.Gson
import com.raywenderlich.busso.server.logging.Logger
import com.raywenderlich.busso.server.model.BusStop
import com.raywenderlich.busso.server.repository.BusStopRepository

/**
 * The Path for the data file
 */
private const val BUS_STOP_RESOURCE_PATH = "/data/bus_stop_list.json"

/**
 * BusStopRepository implementation which picks some data from the configuration file
 */
class ResourceBusStopRepository constructor(
  private val logger: Logger
) : BusStopRepository {

  private val model: BusStopData

  init {
    logger.log("Initializing ResourceBusStopRepository: $this")
    val jsonAsText = this::class.java.getResource(BUS_STOP_RESOURCE_PATH).readText()
    model = Gson().fromJson(jsonAsText, BusStopData::class.java).apply {
      items.forEach { butStop ->
        this@apply.stopMap[butStop.id] = butStop
      }
    }
  }

  override suspend fun findBusStopByLocation(
    latitude: Float,
    longitude: Float,
    radius: Int
  ): List<BusStop> {
    logger.log("findBusStopByLocation on $this with lat:$latitude lon: $longitude")
    return mutableListOf<BusStop>().apply {
      (2..10).forEach {
        add(model.items[it])
      }
    }.sortedBy { busStop -> busStop.distance }
  }


  override suspend fun findBusStopById(budStopId: String): BusStop? =
    model.stopMap[budStopId]
}

/**
 * Local model for the Json file in resources
 */
private data class BusStopData(
  val version: String = "",
  val items: List<BusStop> = emptyList(),
  val stopMap: MutableMap<String, BusStop> = mutableMapOf()
)