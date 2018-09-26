package com.aurelpaulovic.restaurants.server.module

import com.aurelpaulovic.restaurants.service.{DbRestaurantPersistence, RestaurantPersistence}
import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule

object RestaurantPersistenceModule extends TwitterModule {
  @Provides
  @Singleton
  def providesRestaurantPersistence(): RestaurantPersistence = {
    new DbRestaurantPersistence()
  }
}
