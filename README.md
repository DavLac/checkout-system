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

## Endpoints

You can find all checkpoints on swagger url : `http://localhost:8080/swagger-ui/#/`

### Products
- POST /products
- GET /products/{id}
- PATCH /products/{id} (price & description)
- DELETE /products/{id}

### Product deals
- POST /product-deals
- DELETE /product-deals/{id}

### Basket products
- POST /basket-products/add
- PATCH /basket-products/products/{productId}
- DELETE /basket-products/products/{productId}
- POST /basket-products/calculate-total

## Product deals helper
### Grouped discount
- Buy 5, 2 50%
```
POST /product-deals
{
  "discount": {
    "discountPercentage": 50,
    "totalDiscountedItems": 2,
    "totalFullPriceItems": 3
  },
  "productId": 123
}
```
### Direct discount
- Product discount = 25%
```
POST /product-deals
{
  "discount": {
    "discountPercentage": 25,
    "totalDiscountedItems": 1,
    "totalFullPriceItems": 0
  },
  "productId": 123
}
```
### Bundles
- Buy 1 laptop, 1 mouse and 1 keyboard free
```
POST /product-deals
{
  "bundles": [
    {
      "discountPercentage": 100,
      "productId": [mouse ID]
    },
    {
      "discountPercentage": 100,
      "productId": [keyboard ID]
    }
  ],
  "productId": [laptop ID]
}
```

## Special cases
- Only 1 discount allowed by product
- Only 1 bundle allowed by product
- Each product max quantity in a bundle = 1. We cannot add the product himself in a bundle
- If a bundle and a discount target the same product, the smallest price will be chosen for the total price

## To improve
- Discounts with currency (no just by percentage)
- Active/desactive discount
- Add discount expiring date
- Add multiple bundles for the same product
- Implement an external database instead of H2 in-memory

## Jacoco test coverage
Command line :
```
mvn clean test
```
Jacoco report location :  `target/jacoco`

### Test coverage :
(models and DTOs excluded)
- Unit tests : 98%
- Integration tests : 97%
- Merged : 99%
