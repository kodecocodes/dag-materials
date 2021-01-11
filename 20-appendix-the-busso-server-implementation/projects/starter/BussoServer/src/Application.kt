package com.raywenderlich.busso.server

import com.raywenderlich.busso.server.endpoints.findBusArrivals
import com.raywenderlich.busso.server.endpoints.findBusStop
import com.raywenderlich.busso.server.endpoints.myLocation
import com.raywenderlich.busso.server.endpoints.weather
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
  install(Locations)

  install(ContentNegotiation) {
    gson {
      setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    }
  }

  routing {
    get("/") {
      call.respondText("I'm working!!", contentType = ContentType.Text.Plain)
    }
    // Features
    findBusStop()
    findBusArrivals()
    myLocation()
    weather()
  }
}