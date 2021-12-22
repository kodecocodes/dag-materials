package com.raywenderlich.busso.server.model

/**
 * The model for the BusStop
 */
data class BusStop(
    val id: String,
    val name: String,
    val location: GeoLocation,
    val direction: String?,
    val indicator: String?,
    val distance: Float?
)