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

Database Schema
Users Table
Table Name: users
Description: Stores user information.
Fields:
id (BIGINT, Primary Key, Auto Increment) - Unique identifier for the user.
username (VARCHAR(100), Not Null, Unique) - Username of the user.
password (VARCHAR(255), Not Null) - Encrypted password of the user.
email (VARCHAR(100), Not Null, Unique) - Email address of the user.
country (VARCHAR(100), Not Null) - Country of the user.
role (VARCHAR(50), Not Null) - Role of the user (e.g., USER, EMPLOYEE, ADMIN).
Books Table
Table Name: books
Description: Stores book details.
Fields:
bookId (BIGINT, Primary Key, Auto Increment) - Unique identifier for the book.
bookName (VARCHAR(255), Not Null) - Name of the book.
authorName (VARCHAR(255), Not Null) - Name of the book's author.
quantity (INT, Not Null) - Number of copies available in stock.
price (DECIMAL(10, 2), Not Null) - Price of the book.
Cart Items Table
Table Name: cart_items
Description: Stores information about the books in a user's cart.
Fields:
id (BIGINT, Primary Key, Auto Increment) - Unique identifier for the cart item.
quantity (INT, Not Null) - Quantity of the book in the cart.
book_id (BIGINT, Foreign Key) - References the bookId from the books table.
user_id (BIGINT, Foreign Key) - References the id from the users table.
Orders Table
Table Name: orders
Description: Stores information about user orders.
Fields:
id (BIGINT, Primary Key, Auto Increment) - Unique identifier for the order.
user_id (BIGINT, Foreign Key) - References the id from the users table.
totalPrice (DECIMAL(10, 2), Not Null) - Total amount for the order.
orderDate (DATE, Not Null) - Date when the order was placed.
paymentDate (DATE) - Date when the payment was made (nullable).
status (VARCHAR(50), Not Null) - Status of the order (e.g., Pending, Completed, Shipped).
Order Items Table
Table Name: order_items
Description: Stores information about the books in a user's order.
Fields:
id (BIGINT, Primary Key, Auto Increment) - Unique identifier for the order item.
bookName (VARCHAR(255), Not Null) - Name of the book in the order item.
price (DECIMAL(10, 2), Not Null) - Price of the book at the time of the order.
quantity (INT, Not Null) - Quantity of the book in the order.
order_id (BIGINT, Foreign Key) - References the id from the orders table.
book_id (BIGINT, Foreign Key) - References the bookId from the books table.
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
Contribution
Deepshikha Vishwakarma
Saket Kumar
Akshata Kale
Chemikala Rama Devi
