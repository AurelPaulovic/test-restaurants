package com.aurelpaulovic.restaurants.service

import com.aurelpaulovic.restaurants.domain
import com.aurelpaulovic.restaurants.service.RestaurantService.CreateRestaurantException
import com.typesafe.scalalogging.Logger
import monix.eval.Task

trait RestaurantService {
  def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  def getRestaurantsList: Task[Seq[domain.Restaurant]]

  def deleteRestaurant(id: domain.RestaurantId): Task[Unit]

  def createRestaurant(name: String, phone: domain.PhoneNumber, address: domain.Address, description: Option[domain.Description], cuisines: Seq[domain.Cuisine]): Task[domain.Restaurant]

  def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]]

  def healthCheck: Task[Boolean]
}

class RestaurantServiceImpl (persistence: RestaurantPersistence) extends RestaurantService {
  import RestaurantService.logger

  override def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]] = {
    persistence.getRestaurant(id)
  }

  override def getRestaurantsList: Task[Seq[domain.Restaurant]] = {
    persistence.getRestaurants
  }

  override def deleteRestaurant(id: domain.RestaurantId): Task[Unit] = {
    persistence.deleteRestaurant(id).map { maybeRestaurant =>
      maybeRestaurant.foreach(restaurant => logger.info(s"Deleted restaurant [$restaurant]"))
    }
  }

  override def createRestaurant(name: String, phone: domain.PhoneNumber, address: domain.Address, description: Option[domain.Description], cuisines: Seq[domain.Cuisine]): Task[domain.Restaurant] = {
    val restaurantToInsert = domain.Restaurant(
      id = domain.RestaurantId.random,
      name = name,
      cuisines = cuisines,
      phone = phone,
      address = address,
      description = description
    )

    persistence.createRestaurant(restaurantToInsert)
      .flatMap {
        case Left(RestaurantPersistence.CreateRestaurantError.IdIsAlreadyUsed) =>
          Task.raiseError(new CreateRestaurantException("Non-unique restaurant ID"))
        case Right(insertedRestaurant) =>
          Task.now(insertedRestaurant)
      }
  }

  override def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]] = {
    persistence.updateRestaurant(restaurant)
  }

  override def healthCheck: Task[Boolean] = {
    persistence.healthCheck
  }
}

object RestaurantService {
  private[service] val logger = Logger[RestaurantService]

  case class CreateRestaurantException (msg: String) extends RuntimeException(msg)
}