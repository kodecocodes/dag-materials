package com.raywenderlich.busso.server.di

import com.raywenderlich.busso.server.repository.BusArrivalRepository
import com.raywenderlich.busso.server.repository.BusStopRepository
import com.raywenderlich.busso.server.repository.impl.RandomBusArrivalRepository
import com.raywenderlich.busso.server.repository.impl.ResourceBusStopRepository
import org.koin.dsl.module

/** The Module for the object you want to inject */
val repositoryModule = module {
  single<BusStopRepository> { ResourceBusStopRepository(get()) }
  single<BusArrivalRepository> { RandomBusArrivalRepository(get(), get()) }
}