package com.aurelpaulovic.restaurants.domain

import java.util.UUID

case class RestaurantId (value: UUID) extends AnyVal

object RestaurantId {
  def random: RestaurantId = {
    RestaurantId(UUID.randomUUID())
  }
}