package com.aurelpaulovic.restaurants.domain

case class Restaurant (
  id: RestaurantId,
  name: String,
  cuisines: Seq[Cuisine],
  phone: PhoneNumber,
  address: Address,
  description: Option[Description]
)
