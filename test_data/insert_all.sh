#!/usr/bin/env bash

curl -H "Content-Type:application/json" --data @restaurant1.json localhost:8888/restaurants
echo ""
curl -H "Content-Type:application/json" --data @restaurant2.json localhost:8888/restaurants
echo ""
curl -H "Content-Type:application/json" --data @restaurant3.json localhost:8888/restaurants
echo ""
