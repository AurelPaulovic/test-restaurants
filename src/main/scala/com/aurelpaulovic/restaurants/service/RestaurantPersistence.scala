package com.aurelpaulovic.restaurants.service

import java.util.UUID

import com.aurelpaulovic.restaurants.domain
import com.aurelpaulovic.restaurants.service.RestaurantPersistence.CreateRestaurantError.IdIsAlreadyUsed
import com.typesafe.scalalogging.Logger
import doobie.util.transactor.Transactor
import monix.eval.Task

trait RestaurantPersistence {
  import RestaurantPersistence._

  def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  def getRestaurants: Task[Seq[domain.Restaurant]]

  def deleteRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  def updateRestaurant(restaurant: domain.Restaurant): Task[Option[domain.Restaurant]]

  def createRestaurant(restaurant: domain.Restaurant): Task[Either[CreateRestaurantError, domain.Restaurant]]

  def healthCheck: Task[Boolean]
}

object RestaurantPersistence {
  private[service] val logger = Logger[RestaurantPersistence]

  sealed trait CreateRestaurantError

  object CreateRestaurantError {
    case object IdIsAlreadyUsed extends CreateRestaurantError
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
      .attempt
      .transact(transactor)
      .map{
        case Left(error) =>
          logger.error(s"Database healthcheck failed with error: ${error}")
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