SAGA choreography pattern for distributed transaction, meaning that
the tasks are executed independently. Once one task is completed,
it invokes the next tasks in the sequence. In case if the next task 
fails then it invokes the compensationg tasks for the previous tasks.

**Business use case**
Let's consider the example of a order management application.
A customer places an order and the order gets deliver to the customer.
This is the business use case.
Let's say there are four different microservices to take care of this flow.
An order microservice which handles the customer orders.
A payment microservice which handles payments for the orders.
An inventoryDto microservice which updates the inventoryDto once orders are placed.
A delivery microservice which deals with delivering the orders.

**Implementation approach**
we will go with this design to understand SAGA design pattern.
Now let's consider the below functions in each microservice when a customer places an order:
1. createOrder() - Oder microservice
2. procressPayment() - Payment microservice
3. updateStock() - Stock microservice
4. deliverOrder() - Delivery Microservice
   When a customer places an order and doOrder(), doPayment() methods succeed and updateStock() method
   fails then the system will have a wrong inventoryDto information. And the customer won't get her order deliver.
   So all these tasks have to be part of a single transaction. We will use SAGA design pattern to implement
   distributed transaction.
   To resolve the above issue, we need to rollback the entire transaction using backward recovery.
   we need to implement a compensation task for each of the tasks above.
   Here are the compensating tasks
1. reverseOrder() - Order microservice
   2.reversePayment() - Payment microservice
   3.reverseStock() - Stock microservice


Flow of Order Management microservice

Order Microservice createOrder() reverseOrder() ->
Payment Microservice procesPayment() reversePayment() ->
Inventory Microservice updateInventory() reverseInventory() ->
Delivery Microservice deleiverOrder()

First we have to add inventoryDto items to be able to make orders:

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