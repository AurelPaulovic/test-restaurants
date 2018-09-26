package com.aurelpaulovic.restaurants

import com.aurelpaulovic.restaurants.server.controller.RestaurantController
import com.aurelpaulovic.restaurants.server.module.RestaurantServiceModule
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter

object Server extends FinatraServer

class FinatraServer extends HttpServer {
  override val modules = Seq(
    RestaurantServiceModule
  )

  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .add[RestaurantController]

    ()
  }
}
