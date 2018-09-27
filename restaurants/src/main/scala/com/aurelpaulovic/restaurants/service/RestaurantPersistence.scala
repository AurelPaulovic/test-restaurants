package com.aurelpaulovic.restaurants.service

import java.util.UUID

import com.aurelpaulovic.restaurants.domain
import com.aurelpaulovic.restaurants.service.RestaurantPersistence.CreateRestaurantError.IdIsAlreadyUsed
import com.typesafe.scalalogging.Logger
import doobie.util.transactor.Transactor
import monix.eval.Task

trait RestaurantPersistence {
  import RestaurantPersistence._

  /** Searches for a restaurant based on its id and returns it if found
    *
    * @param id the id of the restaurant
    * @return `Some` of the found restaurant, or `None` if there is no restaurant with given `id`
    */
  def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  /** Lists all stored restaurants sorted by their id
    *
    * @return sequence of all stored restaurants
    */
  def getRestaurants: Task[Seq[domain.Restaurant]]

  /** Deletes a restaurant based on its id and returns the deleted restaurant
    *
    * @param id the id of the restaurant
    * @return `Some` of the deleted restaurant, `None` if there was no restaurant with fiven `id`
    */
  def deleteRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  /** Updates a restaurant based on its id and returns the resulting restaurant
    *
    * @param restaurant the restaurant with its updated data
    * @return `Some` of the updated restaurant, `None` if the restaurant does not exist
    */
  def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]]

  /** Persists the restaurant
    *
    * @param restaurant the restaurant to persist
    * @return `Right` of the persisted restaurant, `Left` in case of an error
    */
  def createRestaurant(restaurant: domain.Restaurant): Task[Either[CreateRestaurantError, domain.Restaurant]]

  /** Performs health check
    *
    * @return `true` if the service is ok, `false` otherwise
    */
  def healthCheck: Task[Boolean]
}

object RestaurantPersistence {
  private[service] val logger = Logger[RestaurantPersistence]

  /** Errors that can happen when persisting new restaurants
    */
  sealed trait CreateRestaurantError

  object CreateRestaurantError {
    /** Restaurant with given id already exists
      */
    final case object IdIsAlreadyUsed extends CreateRestaurantError
  }
}

class DbRestaurantPersistence (transactor: Transactor.Aux[Task, Unit]) extends RestaurantPersistence {
  import RestaurantPersistence._
  import DbRestaurantPersistence._
  import doobie._
  import doobie.implicits._
  import doobie.postgres.implicits._
  import cats.implicits._

  override def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]] = {
    sql"SELECT id, name, cuisines, phone, address, description FROM restaurants WHERE id = ${id.value}"
      .query[RestaurantRow]
      .map(_.toDomain)
      .option
      .transact(transactor)
  }


  override def getRestaurants: Task[Seq[domain.Restaurant]] = {
    sql"SELECT id, name, cuisines, phone, address, description FROM restaurants ORDER BY id"
      .query[RestaurantRow]
      .map(_.toDomain)
      .to[List]
      .transact(transactor)
  }

  override def deleteRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]] = {
    sql"DELETE FROM restaurants WHERE id = ${id.value}"
      .update
      .withGeneratedKeys[RestaurantRow]("id", "name", "cuisines", "phone", "address", "description")
      .map(_.toDomain)
      .compile.toList
      .transact(transactor)
      .map(_.headOption) // uuid is primary/unique so we will have at most 1 result
  }

  override def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]] = {
    val row = RestaurantRow.fromDomain(restaurant)

    sql"UPDATE restaurants SET name = ${row.name}, cuisines = ${row.cuisines}, phone = ${row.phone}, address = ${row.phone}, description = ${row.description} WHERE id = ${row.id}"
      .update
      .withGeneratedKeys[RestaurantRow]("id", "name", "cuisines", "phone", "address", "description")
      .map(_.toDomain)
      .compile.toList
      .transact(transactor)
      .map(_.headOption) // uuid is primary/unique so we will have at most 1 result
  }

  override def createRestaurant(restaurant: domain.Restaurant): Task[Either[CreateRestaurantError, domain.Restaurant]] = {
    val row = RestaurantRow.fromDomain(restaurant)

    sql"INSERT INTO restaurants (id, name, cuisines, phone, address, description) VALUES (${row.id}, ${row.name}, ${row.cuisines}, ${row.phone}, ${row.address}, ${row.description})"
      .update
      .withUniqueGeneratedKeys[RestaurantRow]("id", "name", "cuisines", "phone", "address", "description")
      .map(_.toDomain)
      .attemptSomeSqlState {
        case doobie.postgres.sqlstate.class23.UNIQUE_VIOLATION => IdIsAlreadyUsed
      }
      .transact(transactor)
  }

  override def healthCheck: Task[Boolean] = {
    sql"SELECT version()"
      .query[String]
      .unique
      .transact(transactor)
      .attempt
      .map{
        case Left(error) =>
          logger.error(s"Database healthcheck failed with error: ${error.getMessage}")
          false
        case Right(_) =>
          true
      }
  }
}

object DbRestaurantPersistence {
  private case class RestaurantRow (
     id: UUID,
     name: String,
     cuisines: List[String],
     phone: String,
     address: String,
     description: Option[String]
  ) {
    def toDomain: domain.Restaurant = {
      domain.Restaurant(
        id = domain.RestaurantId.fromUUID(id),
        name = name,
        cuisines = cuisines,
        phone = phone,
        address = address,
        description = description
      )
    }
  }

  private object RestaurantRow {
    def fromDomain(restaurant: domain.Restaurant): RestaurantRow = {
      RestaurantRow(
        id = restaurant.id.value,
        name = restaurant.name,
        cuisines = restaurant.cuisines.toList,
        phone = restaurant.phone,
        address = restaurant.address,
        description = restaurant.description
      )
    }
  }
}