package com.aurelpaulovic.restaurants.server.module

import com.aurelpaulovic.restaurants.service.{DbRestaurantPersistence, RestaurantPersistence}
import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import com.twitter.inject.annotations.Flag
import doobie.util.transactor.Transactor
import monix.eval.Task

object RestaurantPersistenceModule extends TwitterModule {
  val dbNameFlag = flag(name = "db.name", default = "restaurants", help = "Name of database")
  val dbUserFlag = flag(name = "db.user", default = "user", help = "Database user name")
  val dbPassFlag = flag(name = "db.pass", default = "password", help = "Database user password")
  val dbHostPortFlag = flag(name = "db.hostport", default = "localhost:5432", help = "Host and port of the database server")

  @Provides
  @Singleton
  def providesRestaurantPersistence(
    @Flag("db.name") dbName: String,
    @Flag("db.user") dbUser: String,
    @Flag("db.pass") dbPass: String,
    @Flag("db.hostport") dbHostPort: String
  ): RestaurantPersistence = {
    // TODO: we want to use some connection pooling mechanism, e.g. HikariCP

    val mxa = Transactor.fromDriverManager[Task](
      "org.postgresql.Driver", s"jdbc:postgresql://$dbHostPort/$dbName", dbUser, dbPass
    )

    new DbRestaurantPersistence(mxa)
  }
}
