package com.aurelpaulovic.restaurants.server.controller

import java.util.UUID

import cats.instances.uuid
import com.aurelpaulovic.restaurants.domain
import com.aurelpaulovic.restaurants.FinatraServer
import com.aurelpaulovic.restaurants.service.RestaurantPersistence
import com.aurelpaulovic.restaurants.service.RestaurantPersistence.CreateRestaurantError
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.TwitterModule
import com.twitter.inject.server.FeatureTest
import monix.eval.Task
import org.scalamock.scalatest.MockFactory

class RestaurantControllerSpec extends FeatureTest with MockFactory {
  val restaurantPersistenceMock = mock[RestaurantPersistence]

  override val server: EmbeddedHttpServer = {
    new EmbeddedHttpServer(new FinatraServer, verbose = false, disableTestLogging = true)
      .bind[RestaurantPersistence].toInstance(restaurantPersistenceMock)
  }

  test("Empty restaurants list") {
    (restaurantPersistenceMock.getRestaurants _).expects().returning(Task.now(Seq.empty))

    server.httpGet(
      path = "/restaurants",
      headers = Map("Content-Type" -> "application/json"),
      andExpect = Status.Ok,
      withJsonBody = """[]"""
    )
  }

  test("Non-empty restaurants list") {
    val restaurants = Seq(
      domain.Restaurant(
        id = domain.RestaurantId(UUID.fromString("5a252323-b5d3-4d29-a106-8aaa7dcfe362")),
        name = "Restaurant 1",
        cuisines = Seq("cuisine 1", "cuisine 2"),
        phone = "phone",
        address = "address",
        description = None
      ),
      domain.Restaurant(
        id = domain.RestaurantId(UUID.fromString("6f54d317-1485-46f5-b9d3-03399a9d5024")),
        name = "Restaurant 2",
        cuisines = Seq("cuisine 3", "cuisine 4"),
        phone = "phone 2",
        address = "address 2",
        description = Some("description 2")
      ),
    )

    (restaurantPersistenceMock.getRestaurants _).expects().returning(Task.now(restaurants))

    server.httpGet(
      path = "/restaurants",
      headers = Map("Content-Type" -> "application/json"),
      andExpect = Status.Ok,
      withJsonBody =
        """
          |[
          |  {
          |    "id": "5a252323-b5d3-4d29-a106-8aaa7dcfe362",
          |    "name": "Restaurant 1",
          |    "cuisines": [
          |      "cuisine 1",
          |      "cuisine 2"
          |    ],
          |    "phone": "phone",
          |    "address": "address"
          |  },
          |  {
          |    "id": "6f54d317-1485-46f5-b9d3-03399a9d5024",
          |    "name": "Restaurant 2",
          |    "cuisines": [
          |      "cuisine 3",
          |      "cuisine 4"
          |    ],
          |    "phone": "phone 2",
          |    "address": "address 2",
          |    "description": "description 2"
          |  }
          |]
        """.stripMargin
    )
  }

  test("Get non-existing restaurant") {
    val uuid = "84d3faae-4bdf-4c95-b0f1-8c2a709632d0"

    (restaurantPersistenceMock.getRestaurant _).expects(domain.RestaurantId.fromUUID(UUID.fromString(uuid))).returning(Task.now(None))

    server.httpGet(
      path = s"/restaurants/$uuid",
      headers = Map("Content-Type" -> "application/json"),
      andExpect = Status.NotFound
    )
  }

  test("Create new restaurant and read it") {
    @volatile var getResult: Option[domain.Restaurant] = None

    (restaurantPersistenceMock.createRestaurant _).expects(*).onCall { restaurant: domain.Restaurant =>
      getResult = Some(restaurant)
      Task.now(Right(restaurant): Either[CreateRestaurantError, domain.Restaurant])
    }

    (restaurantPersistenceMock.getRestaurant _).expects( where {
      (id: domain.RestaurantId) => {
        getResult.exists(_.id == id)
      }
    }).returning(Task.eval(getResult))

    val result = server.httpPost(
      path = "/restaurants",
      headers = Map("Content-Type" -> "application/json"),
      postBody =
        """
          |{
          |  "name": "Restaurant 1",
          |  "address": "Address 1",
          |  "phone": "Phone 1",
          |  "cuisines": ["cuisine 1"],
          |  "description": "description 1"
          |}
        """.stripMargin,
      andExpect = Status.Created
    )

    val id = result.contentString.stripPrefix("\"").stripSuffix("\"")

    server.httpGet(
      path = s"/restaurants/$id",
      headers = Map("Content-Type" -> "application/json"),
      andExpect = Status.Ok,
      withJsonBody =
        s"""
          |{
          |  "id": "$id",
          |  "name": "Restaurant 1",
          |  "address": "Address 1",
          |  "phone": "Phone 1",
          |  "cuisines": ["cuisine 1"],
          |  "description": "description 1"
          |}
        """.stripMargin
    )
  }

  test("Delete non-existing restaurant") {
    val uuid = "84d3faae-4bdf-4c95-b0f1-8c2a709632d0"

    (restaurantPersistenceMock.deleteRestaurant _).expects(domain.RestaurantId.fromUUID(UUID.fromString(uuid))).returning(Task.now(None))

    server.httpDelete(
      path = s"/restaurants/$uuid",
      headers = Map("Content-Type" -> "application/json"),
      andExpect = Status.Ok
    )
  }

  test("Delete existing restaurant") {
    val uuid = "84d3faae-4bdf-4c95-b0f1-8c2a709632d0"

    val restaurant = domain.Restaurant(
      id = domain.RestaurantId(UUID.fromString("84d3faae-4bdf-4c95-b0f1-8c2a709632d0")),
      name = "Restaurant 1",
      cuisines = Seq("cuisine 1", "cuisine 2"),
      phone = "phone",
      address = "address",
      description = None
    )

    (restaurantPersistenceMock.deleteRestaurant _).expects(domain.RestaurantId.fromUUID(UUID.fromString(uuid))).returning(Task.now(Some(restaurant)))

    server.httpDelete(
      path = s"/restaurants/$uuid",
      headers = Map("Content-Type" -> "application/json"),
      andExpect = Status.Ok
    )
  }

  test("Update non-existing restaurant") {
    val id = "84d3faae-4bdf-4c95-b0f1-8c2a709632d0"

    val restaurant = domain.Restaurant(
      id = domain.RestaurantId(UUID.fromString(id)),
      name = "Restaurant 1",
      cuisines = Seq("cuisine 1", "cuisine 2"),
      phone = "phone",
      address = "address",
      description = None
    )

    (restaurantPersistenceMock.updateRestaurant _).expects(restaurant).returning(Task.now(None))

    server.httpPut(
      path = s"/restaurants",
      headers = Map("Content-Type" -> "application/json"),
      putBody =
        s"""
          |  {
          |    "id": "$id",
          |    "name": "Restaurant 1",
          |    "cuisines": [
          |      "cuisine 1",
          |      "cuisine 2"
          |    ],
          |    "phone": "phone",
          |    "address": "address"
          |  }
        """.stripMargin,
      andExpect = Status.NotFound
    )
  }

  test("Update existing restaurant") {
    val id = "84d3faae-4bdf-4c95-b0f1-8c2a709632d0"

    val restaurant = domain.Restaurant(
      id = domain.RestaurantId(UUID.fromString(id)),
      name = "Restaurant 1",
      cuisines = Seq("cuisine 1", "cuisine 2"),
      phone = "phone",
      address = "address",
      description = None
    )

    (restaurantPersistenceMock.updateRestaurant _).expects(restaurant).returning(Task.eval(Some(restaurant)))

    server.httpPut(
      path = s"/restaurants",
      headers = Map("Content-Type" -> "application/json"),
      putBody =
        s"""
           |  {
           |    "id": "$id",
           |    "name": "Restaurant 1",
           |    "cuisines": [
           |      "cuisine 1",
           |      "cuisine 2"
           |    ],
           |    "phone": "phone",
           |    "address": "address"
           |  }
        """.stripMargin,
      andExpect = Status.Ok
    )
  }
}
