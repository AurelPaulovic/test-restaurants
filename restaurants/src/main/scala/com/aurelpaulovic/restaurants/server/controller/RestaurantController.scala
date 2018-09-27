package com.aurelpaulovic.restaurants.server.controller

import com.aurelpaulovic.restaurants.service.RestaurantService
import com.google.inject.Inject
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import monix.execution.Scheduler.Implicits.global


class RestaurantController @Inject() (restaurantService: RestaurantService) extends Controller with ControllerUtils {
  get("/restaurants") { req: Request =>
    restaurantService
      .getRestaurantsList
      .map(_.map(output.Restaurant.fromDomain))
      .runAndConvert
  }

  get("/restaurants/:id") { req: input.GetRestaurant =>
    restaurantService
      .getRestaurant(req.restaurantId)
      .map {
        case None =>
          response.notFound
        case Some(restaurant) =>
          response.ok(output.Restaurant.fromDomain(restaurant))
      }
      .runAndConvert
  }

  post("/restaurants") { req: input.CreateRestaurant =>
    restaurantService
      .createRestaurant(req.name, req.phone, req.address, req.description, req.cuisines)
      .map(restaurant => response.created(output.RestaurantId.fromDomain(restaurant.id)))
      .runAndConvert
  }

  put("/restaurants/:id") { req: input.UpdateRestaurant =>
    restaurantService
      .updateRestaurant(req.restaurant)
      .map {
        case None =>
          response.notFound
        case Some(_) =>
          response.ok
      }
      .runAndConvert
  }

  delete("/restaurants/:id") { req: input.DeleteRestaurant =>
    restaurantService
      .deleteRestaurant(req.restaurantId)
      .runAndConvert
  }
}
