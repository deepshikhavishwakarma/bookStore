Ebook Shopping
A role-based access e-commerce application where Admins manage books and Users can shop books online. The application is built using Spring Boot and MySQL Database.

Features
Admin:
Create, view, update, and delete books.
Full control over book management.
User:
View available books.
Add books to cart.
Increase or decrease cart item quantity.
Remove items from cart.
Clear the cart.
Proceed to order books and complete the payment process.
Technology Used
Frontend: HTML/CSS/JS + Thymeleaf
Backend: Java, Spring Boot, Spring Security
Database: MySQL
Testing: JUnit, Mockito
Tools & Environment: Maven, Eclipse, STS, Postman, Git & GitHub, SonarQube, Jacoco
Installation Guide
Clone the repository:


Import the project into your IDE (like IntelliJ IDEA, Eclipse).

Update Maven dependencies:

Right-click on project -> Maven -> Update Project
Or run in terminal:

Run the application:

Right-click the project -> Run As -> Spring Boot App
Or use terminal:

Make sure your MySQL database is running and properly configured.

Relationships between Tables
Users ↔ Orders: One-to-many relationship. A user can place multiple orders, but each order belongs to one user.
Books ↔ Cart Items: One-to-many relationship. A book can be added to many cart items, but each cart item refers to only one book.
Users ↔ Cart Items: One-to-many relationship. A user can add multiple books to their cart, but each cart item belongs to one user.
Orders ↔ Order Items: One-to-many relationship. An order can contain multiple order items, but each order item belongs to one order.
Books ↔ Order Items: Many-to-one relationship. A book can appear in many order items, but each order item refers to only one book.

Installation
Clone the repository.
Set up the database using the provided schema.
Configure the application settings (e.g., database connection, environment variables).

Run the application.
Usage Instructions
Users can register, log in, browse books, add books to their cart, and place orders.
Admins can manage books, view orders, and update order statuses.

Steps for Users:

Register as a new user.

Login with your credentials.

Browse and view books.

Add books to the cart.

Proceed to payment: Fill in payment information.

Confirm and order now.

-------------------------------------------------------------Contribution-------------------------------------------------------------
Deepshikha Vishwakarma

Saket Kumar

Akshata Kale

Chemikala Rama Devi
