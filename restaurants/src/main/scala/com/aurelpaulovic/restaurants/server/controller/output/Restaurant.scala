package com.aurelpaulovic.restaurants.server.controller.output

import com.aurelpaulovic.restaurants.domain

case class Restaurant(
  id: RestaurantId,
  name: String,
  address: String,
  phone: String,
  description: Option[String],
  cuisines: Seq[String]
)

object Restaurant {
  def fromDomain(restaurant: domain.Restaurant): Restaurant = {
    Restaurant(
      id = RestaurantId.fromDomain(restaurant.id),
      name = restaurant.name,
      address = restaurant.address,
      phone = restaurant.phone,
      description = restaurant.description,
      cuisines = restaurant.cuisines
    )
  }
}