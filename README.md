# KtorHttpApiTutorial

Educational project based on official tutorial from Jetbrains: https://ktor.io/docs/creating-http-apis.html

### Persistence
Currently the app supports persistence via an H2 database, Ehcache, HikariCP and Exposed frameworks.


Branch hibernate replaces Exposed and Ehcache to Hibernate framework. There's also a major change in the
architecture of the application. Now there are three main layers:
- domain
- data
- server

## Domain layer
Defines business models and provides repository contracts.

## Data layer
Provides cache interfaces and their implementations, as well as implements repository interfaces of the domain layer

## Server layer
Operates with interfaces and models of the Domain layer. This decouples it from implementation details. 

## TO DO
- refactor to use <code>Result\<out T\></code> class in domain repositories
- check logic to prohibit a user with a token but without registration to perform any operations on the server
- improve HikariCP configuration 

### Security

The app uses JWT tokens to provide secure authentication and authorization.
The tokens are signed with HMAC256 algorithm and a secret.

The secret must be provided via an environment variable JWT_SECRET.

To use the servers endpoints, a user needs to accompany each request (except those to signup and signin) with a header:
````text
Authorization: Bearer YOUR_TOKEN
````

If the authorization fails, the user will receive a 401 Unauthorized response. 

## Endpoints

### POST /signup
To signup a client needs to POST: 
```json
{
  "username": "CLIENT_USERNAME",
  "password": "CLIENT_PASSWORD"
}
```
If the user with the same username exists, the server will not accept the request.
Password must be greater than 8 characters.

### POST /signin
To signin a client needs to POST:
```json
{
  "username": "CLIENT_USERNAME",
  "password": "CLIENT_PASSWORD"
}
```
If the credentials are correct, the server will respond with:
```json
{
  "token": "JWT_TOKEN",
  "expiresIn": 0
}
```

The field *expiresAt* contains a <code>Long</code> variable, that signifies the period of validity of the token. 

### POST /customer

```json
{
  "id": "200",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@company.com"
}
```
Saves a customer to the DB if the customer with the same id is not present in the DB already.

### GET /customer
Retrieves a list of all customers that are stored on the server.

### GET /customer/{id}
Retrieves a customer if it is present.

### DELETE /customer/{id}
Deletes a customer if it is present.

### POST /order
Saves an Order to the DB if the order is not already present.

### GET /order
Retrieves a list of orders stored on the server

### GET /order/{id}
Retrieves an order if it is present.

### GET /order/{id}/total
Retrieve a total price of all items in the order with a given id if it is present on the server. 