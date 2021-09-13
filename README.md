# Checkout system

This application allows :
- creating products,
- applying deals and discounts on them,
- adding products in a basket and
- calculate the total price

This is a microservice application generated with [Springboot](https://github.com/spring-guides/gs-spring-boot).

## Readme links

- [Instructions](README-TASK.md)
- [How to run the app](README-RUN.md)

## Database

This application uses an **H2 database** configured to persist data in a local file.
When the application is running, a database file is created at this path : `src/main/resources/h2data/database.mv.db`.
To query the database, there is an admin console at this url : `localhost:8080/h2-console`
  
Before connecting to the admin console, fill these informations in the form :
- JDBC URL : `jdbc:h2:file:./src/main/resources/h2data/database`
- user : `user`
- password : `password`

## Database model

![db model](/src/main/resources/static/img/database-model.png)

## Endpoints

You can find all checkpoints on swagger url : `http://localhost:8080/swagger-ui/#/`

### Products
- POST /products
- GET /products/{id}
- PATCH /products/{id} (price & description)
- DELETE /products/{id}

## Jacoco test coverage
Command line :
```
mvn clean test
```
Jacoco report location :  `target/jacoco`

### Test coverage :
- Overall : in progress...
- Unit tests : in progress...
- Integration tests : in progress...
