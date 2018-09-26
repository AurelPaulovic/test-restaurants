package com.aurelpaulovic.restaurants.server.module

import com.aurelpaulovic.restaurants.service.{RestaurantPersistence, RestaurantService, RestaurantServiceImpl}
import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule

object RestaurantServiceModule extends TwitterModule {
  override val modules = Seq(
    RestaurantPersistenceModule
  )

  @Provides
  @Singleton
  def providesRestaurantService(restaurantPersistence: RestaurantPersistence): RestaurantService = {
    new RestaurantServiceImpl(restaurantPersistence)
  }
}
