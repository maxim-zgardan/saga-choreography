SAGA choreography pattern for distributed transaction

First we have to add stock items to be able to make orders:
POST http://localhost:8084/api/addItems
Content-Type: application/json

{
"item": "book",
"quantity": 10
}

After that we can make orders: 

POST http://localhost:8080/api/orders
Content-Type: application/json

{
"address": "Some address",
"item": "book",
"paymentMode": "Credit card",
"quantity": 5,
"amount": 123
}

DBs:
jdbc:h2:mem:orderdb
jdbc:h2:mem:paymentdb
jdbc:h2:mem:deliverydb
jdbc:h2:mem:stockdb