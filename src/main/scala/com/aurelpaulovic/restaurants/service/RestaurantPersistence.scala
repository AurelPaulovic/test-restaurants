package com.aurelpaulovic.restaurants.service

import java.util.UUID

import com.aurelpaulovic.restaurants.domain
import doobie.util.transactor.Transactor
import monix.eval.Task

trait RestaurantPersistence {
  def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]]

  def getRestaurants(): Task[Seq[domain.Restaurant]]
}

class DbRestaurantPersistence (transactor: Transactor.Aux[Task, Unit]) extends RestaurantPersistence {
  import DbRestaurantPersistence._
  import doobie._
  import doobie.implicits._
  import doobie.postgres.implicits._

  override def getRestaurant(id: domain.RestaurantId): Task[Option[domain.Restaurant]] = {
    val uuid = id.value

    sql"SELECT id, name, cuisines, phone, address, description FROM restaurants WHERE id = $uuid"
      .query[RestaurantRow]
      .map(_.toDomain)
      .option
      .transact(transactor)
  }


  override def getRestaurants(): Task[Seq[domain.Restaurant]] = {
    sql"SELECT id, name, cuisines, phone, address, description FROM restaurants ORDER BY id"
      .query[RestaurantRow]
      .map(_.toDomain)
      .to[List]
      .transact(transactor)
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
}