#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

curl -H "Content-Type:application/json" --data @${DIR}/restaurant1.json localhost:8888/restaurants
echo ""
curl -H "Content-Type:application/json" --data @${DIR}/restaurant2.json localhost:8888/restaurants
echo ""
curl -H "Content-Type:application/json" --data @${DIR}/restaurant3.json localhost:8888/restaurants
echo ""
