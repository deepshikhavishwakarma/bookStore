package com.hcl.controller;

import com.hcl.entity.Book;
import com.hcl.entity.User;
import com.hcl.exception.*;
import com.hcl.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestControllers {

    private static final Logger logger = LoggerFactory.getLogger(TestControllers.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderService orderService;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        logger.info("Login attempt for user: {}", loginData.get("username"));
        try {
            userService.login(loginData.get("username"), loginData.get("password"));
            return ResponseEntity.ok("Login successful");
        } catch (UserNotFoundException | InvalidPasswordException e) {
            logger.warn("Login failed for user: {}", loginData.get("username"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Registering user: {}", user.getUsername());
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful");
        } catch (UserAlreadyExistsException e) {
            logger.warn("Registration failed for user: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // GET ALL BOOKS
    @GetMapping("/books")
    public List<Book> getBooks() {
        logger.info("Fetching all books");
        return bookService.getAllBooks();
    }

     
    @PostMapping("/books")
    public ResponseEntity<?> saveBook(@RequestBody Book book) {
    bookService.saveBook(book);
    return ResponseEntity.ok("Book saved");
    }


    // UPDATE BOOK
    @PutMapping("/books")
    public ResponseEntity<?> updateBook(@RequestBody Book book) {
        logger.info("Updating book ID: {}", book.getBookId());
        bookService.saveBook(book);
        return ResponseEntity.ok("Book updated");
    }

    // DELETE BOOK
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        logger.info("Deleting book ID: {}", id);
        bookService.deleteBookById(id);
        return ResponseEntity.ok("Book deleted");
    }

    // SEARCH BOOKS
    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam("keyword") String keyword) {
        logger.info("Searching books with keyword: {}", keyword);
        return bookService.searchBooks(keyword);
    }

    // CART - Add Item
    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestParam Long userId, @RequestParam Long bookId, @RequestParam int quantity) {
        logger.info("Adding book {} to cart for user {}", bookId, userId);
        try {
            shoppingCartService.addItem(userId, bookId, quantity);
            return ResponseEntity.ok("Item added to cart");
        } catch (Exception e) {
            logger.error("Error adding to cart:", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // CART - View
    @GetMapping("/cart")
    public ResponseEntity<?> viewCart(@RequestParam Long userId) {
        logger.info("Viewing cart for user {}", userId);
        return ResponseEntity.ok(Map.of(
                "items", shoppingCartService.getCartItems(userId),
                "totalPrice", shoppingCartService.getTotalPrice(userId)
        ));
    }

    // PLACE ORDER
    @PostMapping("/order/place")
    public ResponseEntity<?> placeOrder(
            @RequestParam Long userId,
            @RequestParam String cardNumber,
            @RequestParam String expiryDate,
            @RequestParam String cvv) {
        logger.info("Placing order for user {}", userId);
        try {
            orderService.placeOrder(userId, null, cardNumber, expiryDate, cvv);
            return ResponseEntity.ok("Order placed successfully");
        } catch (Exception e) {
            logger.error("Error placing order:", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order failed: " + e.getMessage());
        }
    }
}






//package com.hcl.controller;
//
//import com.hcl.entity.Book;
//import com.hcl.entity.User;
//import com.hcl.exception.*;
//import com.hcl.service.*;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/test")
//public class TestControllers {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private BookService bookService;
//
//    @Autowired
//    private ShoppingCartService shoppingCartService;
//
//    @Autowired
//    private OrderService orderService;
//
//    // LOGIN
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
//        try {
//            userService.login(loginData.get("username"), loginData.get("password"));
//            return ResponseEntity.ok("Login successful");
//        } catch (UserNotFoundException | InvalidPasswordException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//    }
//
//    // REGISTER
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody User user) {
//        try {
//            userService.registerUser(user);
//            return ResponseEntity.ok("Registration successful");
//        } catch (UserAlreadyExistsException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
//        }
//    }
//
//    // GET ALL BOOKS
//    @GetMapping("/books")
//    public List<Book> getBooks() {
//        return bookService.getAllBooks();
//    }
//
//    // SAVE BOOK
//    @PostMapping("/books")
//    public ResponseEntity<?> saveBook(@RequestBody Book book) {
//        bookService.saveBook(book);
//        return ResponseEntity.ok("Book saved");
//    }
//
//    // UPDATE BOOK
//    @PutMapping("/books")
//    public ResponseEntity<?> updateBook(@RequestBody Book book) {
//        bookService.saveBook(book);
//        return ResponseEntity.ok("Book updated");
//    }
//
//    // DELETE BOOK
//    @DeleteMapping("/books/{id}")
//    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
//        bookService.deleteBookById(id);
//        return ResponseEntity.ok("Book deleted");
//    }
//
//    // SEARCH BOOKS
//    @GetMapping("/books/search")
//    public List<Book> searchBooks(@RequestParam("keyword") String keyword) {
//        return bookService.searchBooks(keyword);
//    }
//
//    // CART - Add Item
//    @PostMapping("/cart/add")
//    public ResponseEntity<?> addToCart(@RequestParam Long userId, @RequestParam Long bookId, @RequestParam int quantity) {
//        try {
//            shoppingCartService.addItem(userId, bookId, quantity);
//            return ResponseEntity.ok("Item added to cart");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    // CART - View
//    @GetMapping("/cart")
//    public ResponseEntity<?> viewCart(@RequestParam Long userId) {
//        return ResponseEntity.ok(Map.of(
//                "items", shoppingCartService.getCartItems(userId),
//                "totalPrice", shoppingCartService.getTotalPrice(userId)
//        ));
//    }
//
//    // PLACE ORDER
//    @PostMapping("/order/place")
//    public ResponseEntity<?> placeOrder(
//            @RequestParam Long userId,
//            @RequestParam String cardNumber,
//            @RequestParam String expiryDate,
//            @RequestParam String cvv) {
//
//        try {
//            orderService.placeOrder(userId, null, cardNumber, expiryDate, cvv);
//            return ResponseEntity.ok("Order placed successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order failed: " + e.getMessage());
//        }
//    }
//}
//
//
//
//
//
//
//
//
////package com.hcl.controller;
////
////import com.hcl.entity.Book;
////import com.hcl.entity.User;
////import com.hcl.exception.*;
////import com.hcl.service.*;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.http.*;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.List;
////import java.util.Map;
////
////@RestController
////@RequestMapping("/api/test")
////public class TestControllers {
////
////    @Autowired
////    private UserService userService;
////
////    @Autowired
////    private BookService bookService;
////
////    @Autowired
////    private ShoppingCartService shoppingCartService;
////
////    @Autowired
////    private OrderService orderService;
////
////    // LOGIN
////    @PostMapping("/login")
////    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
////        try {
////            userService.login(loginData.get("username"), loginData.get("password"));
////            return ResponseEntity.ok("Login successful");
////        } catch (UserNotFoundException | InvalidPasswordException e) {
////            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
////        }
////    }
////
////    // REGISTER
////    @PostMapping("/register")
////    public ResponseEntity<?> register(@RequestBody User user) {
////        try {
////            userService.registerUser(user);
////            return ResponseEntity.ok("Registration successful");
////        } catch (UserAlreadyExistsException e) {
////            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
////        }
////    }
////
////    // GET ALL BOOKS
////    @GetMapping("/books")
////    public List<Book> getBooks() {
////        return bookService.getAllBooks();
////    }
////
////    // SAVE BOOK
////    @PostMapping("/books")
////    public ResponseEntity<?> saveBook(@RequestBody Book book) {
////        bookService.saveBook(book);
////        return ResponseEntity.ok("Book saved");
////    }
////
////    // UPDATE BOOK
////    @PutMapping("/books")
////    public ResponseEntity<?> updateBook(@RequestBody Book book) {
////        bookService.saveBook(book);
////        return ResponseEntity.ok("Book updated");
////    }
////
////    // DELETE BOOK
////    @DeleteMapping("/books/{id}")
////    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
////        bookService.deleteBookById(id);
////        return ResponseEntity.ok("Book deleted");
////    }
////
////    // SEARCH BOOKS
////    @GetMapping("/books/search")
////    public List<Book> searchBooks(@RequestParam("keyword") String keyword) {
////        return bookService.searchBooks(keyword);
////    }
////
////    // CART - Add Item
////    @PostMapping("/cart/add")
////    public ResponseEntity<?> addToCart(@RequestParam Long userId, @RequestParam Long bookId, @RequestParam int quantity) {
////        try {
////            shoppingCartService.addItem(userId, bookId, quantity);
////            return ResponseEntity.ok("Item added to cart");
////        } catch (Exception e) {
////            return ResponseEntity.badRequest().body(e.getMessage());
////        }
////    }
////
////    // CART - View
////    @GetMapping("/cart")
////    public ResponseEntity<?> viewCart(@RequestParam Long userId) {
////        return ResponseEntity.ok(Map.of(
////                "items", shoppingCartService.getCartItems(userId),
////                "totalPrice", shoppingCartService.getTotalPrice(userId)
////        ));
////    }
////
////    // PLACE ORDER
////    @PostMapping("/order/place")
////    public ResponseEntity<?> placeOrder(
////            @RequestParam Long userId,
////            @RequestParam String cardNumber,
////            @RequestParam String expiryDate,
////            @RequestParam String cvv) {
////
////        try {
////            orderService.placeOrder(userId, null, cardNumber, expiryDate, cvv);
////            return ResponseEntity.ok("Order placed successfully");
////        } catch (Exception e) {
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order failed: " + e.getMessage());
////        }
////    }
////}
