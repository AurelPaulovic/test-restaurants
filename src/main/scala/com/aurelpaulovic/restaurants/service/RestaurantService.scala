package com.aurelpaulovic.restaurants.service

import com.aurelpaulovic.restaurants.domain
import monix.eval.Task

trait RestaurantService {
  def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  def getRestaurantsList(): Task[Seq[domain.Restaurant]]

  def deleteRestaurant(id: domain.RestaurantId): Task[Unit]

  def createRestaurant(name: String, phone: domain.PhoneNumber, address: domain.Address, description: Option[domain.Description], cuisines: Seq[domain.Cuisine]): Task[domain.Restaurant]

  def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]]
}

class RestaurantServiceImpl (persistence: RestaurantPersistence) extends RestaurantService {
  override def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]] = {
    Task.now(None: Option[domain.Restaurant])
  }

  override def getRestaurantsList(): Task[Seq[domain.Restaurant]] = {
    Task.now(Seq.empty)
  }

  override def deleteRestaurant(id: domain.RestaurantId): Task[Unit] = {
    Task.unit
  }

  override def createRestaurant(name: String, phone: domain.PhoneNumber, address: domain.Address, description: Option[domain.Description], cuisines: Seq[domain.Cuisine]): Task[domain.Restaurant] = {
    Task.now(
      domain.Restaurant(
        id = domain.RestaurantId.random,
        name = name,
        cuisines = cuisines,
        phone = phone,
        address = address,
        description = description
      )
    )
  }

  override def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]] = {
    Task.now(None: Option[domain.Restaurant])
  }
}