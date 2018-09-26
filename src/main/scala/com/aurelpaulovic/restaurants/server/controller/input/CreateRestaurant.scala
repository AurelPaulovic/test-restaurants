package com.aurelpaulovic.restaurants.server.controller.input

import com.twitter.finatra.validation.NotEmpty

case class CreateRestaurant (
  @NotEmpty name: String,
  @NotEmpty address: String,
  @NotEmpty phone: String,
  description: Option[String] = None,
  cuisines: Seq[String]
)
