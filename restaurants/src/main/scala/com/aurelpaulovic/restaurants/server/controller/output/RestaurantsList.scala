package com.aurelpaulovic.restaurants.server.controller.output

import com.twitter.inject.domain.WrappedValue

case class RestaurantsList (value: Seq[Restaurant]) extends WrappedValue[Seq[Restaurant]]

object RestaurantsList {
  def apply(): RestaurantsList = {
    RestaurantsList(Seq.empty)
  }

  def apply(r1: Restaurant, rs: Restaurant*): RestaurantsList = {
    RestaurantsList(r1 +: rs)
  }
}


