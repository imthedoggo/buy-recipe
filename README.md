# Read me first: buy-recipe
The goal of this project is to manage superheroes and their attributes:
create and fetch superheroes and create new attributes (e.g new powers...).

* Designed simply yet flexible and extendable
* Clean
* Well-documented and well-tested

## Tech stack:
* Kotlin
* Spring 3
* RESTful endpoints 
* Docker to build image & run the project from anywhere
* Docker-compose to start the DB
* Postgres as a relational DB
* Liquibase DB migration tool to set up tables

### Assumptions:
* All the products are always available
* Anyone can manipulate any cart - no user management expected

### Functional requirements:
* get shopping cart content
* get existing recipes
* add a recipe to cart content
* delete a recipe from a cart

### What is implemented:
An API layer that supports:
* list recipes with pagination
* view ingredients and costs
* add recipes to cart
* create new recipes

![model_relationships.png](model_relationships.png)

### Current limitations:
* 
*

### Future improvements:

#### Business-wise
* Set up and manage application users, with authentication (e.g. access with JWT token) and authorization, to access their cart only
* Sophisticated mapping of products-ingredients, in terms of quantity (units os measure, optimize how much to buy)
* Sophisticated mapping of products-ingredients, in terms of priority (which products to buy for the same ingredient)
* Add nutritional info to the products and a better tagging/classification system

#### Technical
* Add OpenAPI spec to document the endpoints
* Implement error handling with consistent error objects (consider JSON API spec as reference)
* Count inventory - after every buy, remove amount of available product
* Introduce a robust pricing type, for example a Price type (BigDecimal, Currency)
* Add an API rate limit for security reasons


## How to run this project:
Create a local postgres DB
```
docker-compose up -d
```
I Used a pgadmin client, to interact with the DB (view tables etc.), using the application.yml credentials.

Start the spring project
```
./gradlew bootRun
```

### Deploy:
Build Docker image
```
docker build -t imthedoggo/buy-recipe .
```
Run the image locally
```
docker run -p 8081:8080 -d imthedoggo/buy-recipe 
```

### Endpoints:

| Method  | Resource                     | Description              |
|---------|------------------------------|--------------------------|
| POST    | /api/carts/{id}/add-recipe   | Add a new recipe to cart |
| POST    |  |  |
| GET     | /api/carts/{id}         | Get cart by ID           |
| POST    | /api/carts/delete-recipe    | Delete recipe from cart  |


### Examples

#### POST /api/carts/{id}/add-recipe
Request:
```

```
Response:
```
{
}
```

#### POST
Request:
```

```
Response:
```
{
    
}
```
#### POST 
Request:
```
{
   
}
```
Response:
```
{
 
}
```
#### GET 
Request:
```

```
Response:
```
{
    
}
```
