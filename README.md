![AccommodationBookingServiceImage.jpg](AccommodationBookingServiceImage.jpg)

## Introduction

The Accommodation Booking Service is a Java-based web application built using the Spring Boot framework. 
It serves as a comprehensive platform for managing booking, payments, user registrations, accommodations, 
and orders within a booking application. This project was inspired by the need for an efficient management 
so as old service faces significant operational challenges as it relies on antiquated, 
manual processes for managing properties, renters, financial transactions, and booking records. 
All data is documented on physical paperwork, creating inefficiencies and limiting the ability 
to check property availability in real-time. Furthermore, the service only accepts cash payments, 
leaving out the convenience of credit card transactions.

## Technologies Used

* **Spring Boot**: Provides a powerful and flexible framework for building Java-based applications.
* **Spring Security**: Ensures secure user authentication and authorization.
* **Spring Data JPA**: Simplifies the implementation of data access layers by providing a repository abstraction.
* **Swagger**: Enables API documentation and testing.
* **MapStruct**: Facilitates the mapping between DTOs and entity models.
* **Hibernate**: An ORM tool for Java applications.
* **Liquibase**: An open-source database schema migration tool.
* **PostgreSQL**: A relational database management system.
* **Docker**: A platform for developing, shipping, and running applications in containers.
* **Lombok**: A library to reduce boilerplate code in Java.
* **Stripe Java SDK**: Integrates Stripe payment capabilities for handling transactions and billing.

## Functionalities
### User Management

* User registration with optional shipping address.
* Secure user login with JWT-based authentication.

**Available endpoints for User Management**

(for non-authenticated users)
``` 
POST: /api/auth/register
``` 
Example of request body to **register**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "repeatPassword": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```
``` 
POST: /api/auth/login
```
Example of request body to **log-in**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

### Booking Management

* Create, retrieve, update, and delete books.
* Search for books based on various parameters.
* Associate books with multiple categories.

**Available endpoints for Booking Management**

with ADMIN role or USER role
``` 
GET: /api/bookings/my

GET: /api/bookings/{id} 

GET: /api/bookings/{userId}/{status}

POST: /api/bookings

DELETE: /api/bookings/{id}

PUT: /api/bookings/{id}

``` 
Example of request body to **create new booking**:
```json
{
   "checkInDate": "2025-05-19",
   "checkOutDate": "2025-05-22",
   "accommodationId": 3,
   "userId": 1,
   "status": "CONFIRMED"
}
```
If you want to add accommodation to booking, you should crate accommodation first, or update booking later.

To update Booking you should use same request body as for creation of a new book.

### Accommodation Management

* Create, retrieve, update, and delete accommodation for booking.

**Available endpoints for Accommodation Management**

with USER role
``` 
GET: /api/accommodations

GET: /api/accommodations/{id}
```

with ADMIN role
``` 
POST: /api/accommodations 

PUT: /api/accommodations/{id}

DELETE: /api/accommodations/{id}
```
Example of request body to **create new accommodation**:
```json
{
   "type": "HOUSE",
   "location": "123 Ocean Drive, Miami",
   "size": "3 Bedroom",
   "amenities": ["WiFi", "Air Conditioning", "Swimming Pool", "Parking", "Kitchen"],
   "price": 250,
   "dailyRate": 250,
   "availability": 5,
   "isDeleted": false
}
```
### Payment Management

* Create, retrieve, renew, and redirection to Stripe.
* Handles successful or cancel payment processing through Stripe redirection.

**Available endpoints for Payment Management**

with ADMIN or USER role
```
GET: /api/payments

POST: /api/payments

GET: /api/payments/success

GET: /api/payments/cancel

POST: /api/payments/{paymentId}/renew
```
Example of request body to **add payments**:
```json
{
   "userId": 1,
   "amountToPay": 350.00,
   "sessionUrl": "stripe_session_url",
   "status": "PENDING"
}
```
Example of request body to **renew payments**:
```json
{
   "sessionId": "cs_test_a1z0NrQ4o0rmTu7oxbJpH9ECBELWfgKJT2fJYfzjHn5gsViAVVLD8EyLbb",
   "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1z0NrQ4o0rmTu7oxbJpH9ECBELWfgKJT2fJYfzjHn5gsViAVVLD8EyLbb#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
   "bookingId": 1,
   "amountToPay": 1500.00,
   "status": "PENDING"
}
```


## Project Structure
The project follows a modular structure:

* **model**: Entity models representing the database schema.
* **repository**: Spring Data JPA repositories for database operations.
* **service**: Business logic implementation.
* **controller**: Contains controllers for handling HTTP requests.
* **dto**: Data Transfer Objects for communication between the client and server.
* **mapper**: Mapper interfaces for mapping between DTOs and entity models.

Link to a video demonstration of the project - https://drive.google.com/file/d/1ubeYu5qmOiAelYRxKJk2ci6WUY7M4amj/view?usp=sharing

## Setup

To set up and use the Accommodation Booking Service, follow these steps:


1. Clone the repository to your local machine.
2. Configure the database settings in the application properties.
3. Build and run the application using your preferred Java IDE or build tool.
4. Access the Swagger documentation: http://localhost:8080/swagger-ui.html
   The API uses JWT (JSON Web Tokens) for authentication.

   To access protected endpoints first login to api, then include the generated JWT token in the Authorization header of your requests.


## Challenges and Solutions
**Challenge**: Implementing secure user authentication.

**Solution**: Utilized Spring Security and JWT for a robust authentication mechanism.

**Challenge**: Efficiently managing payments, bookings and order processing.

**Solution**: For run payment handling transactions and billing Integrated Stripe payment service.

## Postman
For detailed API usage, you can use provided requests samples.
Examples answers for difference request body:

``` 
POST: /api/auth/register
``` 
Example of request body to **register**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "repeatPassword": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "shippingAddress": "123 Main St, City, Country"
}
```
``` 
Answer from request body to **register**:
{
  "id": 1,
  "password": "securePassword123",
  "repeatPassword": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "shippingAddress": "123 Main St, City, Country"
}
```
```
POST: /api/auth/login
```
Example of request body to **log-in**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```
```json
Answer from request body to **log-in**:
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTcxNzYwMDI4MywiZXhwIjoxNzE3NjAwNTgzfQ.0V8B9GNRiZiFnaxdetrAv9RpIgvxl99q6IqSyqE2lBQ"
}
```
```
POST: /api/payments/
```
Example of request payments to **create new payments**:
```json
{
   "bookingId": 1,
   "userId": 2,
   "amountToPay": 1000.00,
   "sessionUrl": "stripe_session_url",
   "status": "PENDING"
}
```
```json
Answer from request body to **create new payments**:
{
"sessionId": "cs_test_a1nD22YmcDgChQwHE3NnVD4A4cffkaLDltiVYCYpA9g1UCAAbJMt08ICWB",
"sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1nD22YmcDgChQwHE3NnVD4A4cffkaLDltiVYCYpA9g1UCAAbJMt08ICWB#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
"bookingId": 1,
"amountToPay": 1000.00,
"status": "PENDING"
}
```
```
GET: /api/payments
Answer from request body **get all payments**:
```
```json
[
   {
      "id": 1,
      "bookingId": 1,
      "userId": 2,
      "amountToPay": 1000.00,
      "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1nD22YmcDgChQwHE3NnVD4A4cffkaLDltiVYCYpA9g1UCAAbJMt08ICWB#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
      "status": "PENDING"
   },
   {
      "id": 2,
      "bookingId": 2,
      "userId": 2,
      "amountToPay": 4000.00,
      "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a18YmqhnqSyPojsTQtoZ0mYpAVYtPFdEhWvDWcg7CMtEGuzW1o9k7y0Wjq#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
      "status": "PAID"
   },
   {
      "id": 3,
      "bookingId": 3,
      "userId": 2,
      "amountToPay": 750.00,
      "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a166arzyOPbh6uhXejGVXu8WNxDwrKTsJabszPlkyR9DYL6C46cCtdMnM1#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
      "status": "PAID"
   },
   {
      "id": 4,
      "bookingId": 4,
      "userId": 2,
      "amountToPay": 600.00,
      "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1PkSimEmcHCFa81SSFjFdiogrsEcVqoaknr0EfBPtNcR7QySsjhHlYyVY#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
      "status": "PAID"
   },
   {
      "id": 5,
      "bookingId": 5,
      "userId": 2,
      "amountToPay": 450.00,
      "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1kJoa8mzHu6xntV3VqJoyIcFvWntwrZpzpuRJMDvbP06KH36lM45gPflR#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
      "status": "PAID"
   },
   {
      "id": 6,
      "bookingId": 6,
      "userId": 2,
      "amountToPay": 1500.00,
      "sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1Q2JeBYuk6lsKQzZ1AsF2WYRXIpXRnXceBVTU9Fr7ljQCKmwjZgOg44HO#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
      "status": "CANCELLED"
   }
]
```
```

POST: /api/payment/{paymentId}/renew
```
```json
Answer from request body to **create new booking**:
{
"sessionId": "cs_test_a1Q2JeBYuk6lsKQzZ1AsF2WYRXIpXRnXceBVTU9Fr7ljQCKmwjZgOg44HO",
"sessionUrl": "https://checkout.stripe.com/c/pay/cs_test_a1Q2JeBYuk6lsKQzZ1AsF2WYRXIpXRnXceBVTU9Fr7ljQCKmwjZgOg44HO#fidkdWxOYHwnPyd1blpxYHZxWjA0V0ZmVH9VRFRNfGpIfVZvcGl3cWhVVEdIMVNfQWJLf1FtfGhmYEtXRl01NmBzMlBvNFN2bXxLSjdHc0NVZm9qYm5UXHxBcUhfcGxudml9fTIyXUJnckwwNTVkPUFAb0wzaicpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
"bookingId": 6,
"amountToPay": 1500.00,
"status": "PENDING"
}
```
```

## Conclusion
The Accommodation Booking Service is designed to offer a seamless experience for managing booking, payments, user registrations, accommodations, and orders within a booking application.

Whether you're a developer looking to understand the codebase or a user interested in utilizing the features, this README provides a comprehensive guide to get started.
