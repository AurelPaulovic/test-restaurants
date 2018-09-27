# Restaurants

To run the application use the provided docker-compose setup using
```bash
docker-compose up
```
This will start a PostgreSQL server and the `Restarurants` application exposing the applications' API on `localhost:8888`. 
The application runs on Finatra and as such also provides stock admin interface on `localhost:9990`.
The database is exposed on `localhost:5432`.

Once running, you can insert some sample data into the system by running command
```bash
./test_data/insert_all.sh
```

## API
### Create new restaurant
```bash
curl -H "Content-Type: application/json" --data @[create_restaurant_json_file] localhost:8888/restaurants
```
The value returned by the call is the new UUID of the restaurant. The `create_restaurant_json_file` is path to file containing the data 
of a new restaurant in json format.

The format of the restaurant data needed is as follows:
```json
{
    "name": "Restaurant 1",
    "address": "address of Restaurant 1",
    "phone": "phone of Restaurant 1",
    "description": "description of Restaurant 1",
    "cuisines": ["cuisine 1", "cuisine 2", "cuisine 3"]
}
```
The `description` field is optional.

### List existing restaurants
```bash
curl localhost:8888/restaurants
```
### Get restaurant by ID
```bash
curl localhost:8888/restaurants/[id]
```
Where `[id]` is a UUID returned when creating the restaurant.

### Delete restaurant by ID
```bash
curl -XDELETE localhost:8888/resturants/[id]
```
WHERE `[id]` is a UUID returned when creating the restaurant.

### Update restaurant
```bash
curl -XPUT -H "Content-Type: application/json" --data @[update_restaurant_json_file] localhost:8888/restaurants
```
The `update_restaurant_json_file` is path to file containing the update data of an existing restaurant in json format.

The format of the restaurant update data needed is as follows:
```json
{
    "id": "c1aa71c3-9243-4871-ac94-6ce3d2bee97a", 
    "name": "Restaurant 1 updated",
    "address": "address of Restaurant 1 updated",
    "phone": "phone of Restaurant 1",
    "description": "description of Restaurant 1 updated",
    "cuisines": ["cuisine 1", "cuisine 2", "cuisine 3", "cuisine updated"]
}
```
The value for `id` field has to be the uuid of the updated restaurant. 
