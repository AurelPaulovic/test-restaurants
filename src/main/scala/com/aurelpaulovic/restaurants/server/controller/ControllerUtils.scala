package com.aurelpaulovic.restaurants.server.controller

import com.aurelpaulovic.restaurants.server.controller.ControllerUtils.MonixTaskHelper
import com.twitter.finagle.http.ResponseProxy
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future

protected[controller] trait ControllerUtils {
  implicit def toMonixTaskHelper[T](task: Task[T]): MonixTaskHelper[T] = {
    MonixTaskHelper[T](task)
  }
}

object ControllerUtils {
  case class MonixTaskHelper[T] (task: Task[T]) extends AnyVal {
    /** Asynchronously runs the Monix task using provided scheduler and converts it into result that Finatra understands
      * and accepts as controller route' return value
      */
    def runAndConvert(implicit scheduler: Scheduler): Future[ResponseProxy] = {
      task.runAsync(scheduler).asInstanceOf[Future[ResponseProxy]]
    }
  }
}
