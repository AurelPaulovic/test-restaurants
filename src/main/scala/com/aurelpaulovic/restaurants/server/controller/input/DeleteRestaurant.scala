package com.aurelpaulovic.restaurants.server.controller.input

import java.util.UUID

import com.aurelpaulovic.restaurants.domain
import com.twitter.finatra.request.RouteParam

case class DeleteRestaurant (
  @RouteParam id: UUID
) {
  lazy val restaurantId: domain.RestaurantId = {
    domain.RestaurantId(id)
  }
}
