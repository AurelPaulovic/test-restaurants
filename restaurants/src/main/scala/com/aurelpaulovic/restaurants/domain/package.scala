package com.aurelpaulovic.restaurants

package object domain {
  // we would probably want some special types that have validations and proper structure (e.g. for address have street name, city, etc.)
  type Description = String
  type Address = String
  type PhoneNumber = String
  type Cuisine = String
}
