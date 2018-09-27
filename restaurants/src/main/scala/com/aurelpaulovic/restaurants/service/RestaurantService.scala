package com.aurelpaulovic.restaurants.service

import com.aurelpaulovic.restaurants.domain
import com.aurelpaulovic.restaurants.service.RestaurantService.CreateRestaurantException
import com.typesafe.scalalogging.Logger
import monix.eval.Task

trait RestaurantService {
  /** Finds an existing restaurant with id
    *
    * @param id the id of the searched restaurant
    * @return `Some` of the found restaurant, `None` if the restaurant does not exist
    */
  def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  /** Returns a sequence of all existing restaurants
    *
    * @return sequence of existing restaurants
    */
  def getRestaurantsList: Task[Seq[domain.Restaurant]]

  /** Deletes restaurant with id
    *
    * @param id  the id of the restaurant to delete
    * @return `Unit` if the restaurant existed and was successfully deleted or if the restaurant did not exist in the first place
    */
  def deleteRestaurant(id: domain.RestaurantId): Task[Unit]

  /** Creates new restaurant based on the provided data
    *
    * @param name name of the restaurant
    * @param phone phone number of the restaurant
    * @param address address of the restaurant
    * @param description optional description of the restaurant
    * @param cuisines collection of cuisines served by the restaurant
    * @return the created new restaurant
    */
  def createRestaurant(name: String, phone: domain.PhoneNumber, address: domain.Address, description: Option[domain.Description], cuisines: Seq[domain.Cuisine]): Task[domain.Restaurant]

  /** Updates data of an existing restaurant
    *
    * @param restaurant new data of a restaurant
    * @return `Some` of the update restaurant, `None` if the restaurant does not exist
    */
  def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]]

  /** Performs health check
    *
    * @return `true` if the service is ok, `false` otherwise
    */
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
          Task.raiseError(CreateRestaurantException("Non-unique restaurant ID"))
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

  /** Failure when creating a new restaurant
    *
    * @param msg description of the failure
    */
  case class CreateRestaurantException (msg: String) extends RuntimeException(msg)
}