package com.aurelpaulovic.restaurants.server.controller

import com.aurelpaulovic.restaurants.service.RestaurantService
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import monix.execution.Scheduler.Implicits.global

class HealthCheckController @Inject() (restaurantService: RestaurantService) extends Controller with ControllerUtils {
  get("/v1/healthcheck") { _: Request =>
    restaurantService
      .healthCheck
      .map{
        case true =>
          response.ok(output.ServerHealth(restaurantService = true))
        case false =>
          response.serviceUnavailable.body(output.ServerHealth(restaurantService = false))
      }
      .runAndConvert
  }
}
