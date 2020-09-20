package com.raywenderlich.busso.server.repository.impl

import com.google.gson.Gson
import com.raywenderlich.busso.server.model.BusStop
import com.raywenderlich.busso.server.repository.BusStopRepository
import kotlin.random.Random

/**
 * Min number of busStop per request
 */
const val MIN_BUS_STOP_NUMBER = 4

/**
 * Max number of busStop per request
 */
const val MAX_BUS_STOP_NUMBER = 8

/**
 * Number of arrivals for line
 */
fun busStopNumberRange() = 0..Random.nextInt(MIN_BUS_STOP_NUMBER, MAX_BUS_STOP_NUMBER)

/**
 * The Path for the data file
 */
private const val BUS_STOP_RESOURCE_PATH = "/data/bus_stop_list.json"

/**
 * BusStopRepository implementation which picks a random number of BusStop from some data
 * in the configuration file
 */
class ResourceBusStopRepository : BusStopRepository {

    private val model: BusStopData

    init {
        val jsonAsText = this::class.java.getResource(BUS_STOP_RESOURCE_PATH).readText()
        model = Gson().fromJson(jsonAsText, BusStopData::class.java).apply {
            items.forEach { butStop ->
                this@apply.stopMap[butStop.id] = butStop
            }
        }
    }

    override suspend fun findBusStopByLocation(latitude: Float, longitude: Float, radius: Int): List<BusStop> =
        mutableListOf<BusStop>().apply {
            // TODO make this random but dependent on the input position. Otherwise
            // it changes everytime
            (2..10).forEach {
                add(model.items[it])
            }

            /**
            // To remove duplicates.
            val positions = mutableSetOf<Int>()
            busStopNumberRange().forEach {
                val randomPosition = Random.nextInt(0, model.items.size)
                if (!positions.contains(randomPosition)) {
                    positions.add(randomPosition)
                    add(model.items[randomPosition])
                }
            }
            */
        }.sortedBy { busStop -> busStop.distance }

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