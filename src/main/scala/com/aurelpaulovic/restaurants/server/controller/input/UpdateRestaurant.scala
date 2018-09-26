package com.aurelpaulovic.restaurants.server.controller.input

import java.util.UUID

import com.aurelpaulovic.restaurants.domain
import com.twitter.finatra.request.RouteParam
import com.twitter.finatra.validation.NotEmpty

case class UpdateRestaurant (
  @RouteParam id: UUID,
  @NotEmpty name: String,
  @NotEmpty address: String,
  @NotEmpty phone: String,
  description: Option[String] = None,
  cuisines: Seq[String]
) {
  def restaurant: domain.Restaurant = {
    domain.Restaurant(
      id = domain.RestaurantId(id),
      name = name,
      cuisines = cuisines,
      phone = phone,
      address = address,
      description = description
    )
  }
}
