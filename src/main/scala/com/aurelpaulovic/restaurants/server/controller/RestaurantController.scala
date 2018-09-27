package com.aurelpaulovic.restaurants.server.controller

import com.aurelpaulovic.restaurants.service.RestaurantService
import com.google.inject.Inject
import com.twitter.finagle.http.{Request, ResponseProxy}
import com.twitter.finatra.http.Controller
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future

class RestaurantController @Inject() (restaurantService: RestaurantService) extends Controller {
  import RestaurantController._

  get("/restaurants") { req: Request =>
    restaurantService
      .getRestaurantsList()
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

object RestaurantController {
  implicit class MonixTaskHelper[T] (val task: Task[T]) extends AnyVal {
    /** Asynchronously runs the Monix task using provided scheduler and converts it into result that Finatra understands
      * and accepts as controller route' return value
      */
    def runAndConvert(implicit scheduler: Scheduler): Future[ResponseProxy] = {
      task.runAsync(scheduler).asInstanceOf[Future[ResponseProxy]]
    }
  }
}
