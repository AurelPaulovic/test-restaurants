package com.aurelpaulovic.restaurants.server.controller.output

import java.util.UUID

import com.aurelpaulovic.restaurants.domain
import com.twitter.inject.domain.WrappedValue

case class RestaurantId (value: UUID) extends WrappedValue[UUID]

object RestaurantId {
  def fromDomain(id: domain.RestaurantId): RestaurantId = {
    RestaurantId(id.value)
  }
}
