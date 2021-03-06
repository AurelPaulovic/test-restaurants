#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )"/.. >/dev/null && pwd )"

docker run --rm -it -v $DIR/restaurants/src/main/resources/db:/docker-entrypoint-initdb.d:ro -p 5432:5432 -e "POSTGRES_DB=restaurants" -e "POSTGRES_USER=user" -e "POSTGRES_PASSWORD=password" postgres
