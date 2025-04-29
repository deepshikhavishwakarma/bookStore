package com.hcl.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hcl.entity.Book;
import com.hcl.service.BookService;

@Controller
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private static final String REDIRECT_BOOKS = "redirect:/books"; // Define the constant

    
    @Autowired
    private BookService bookService;

    @GetMapping({"/","","/home"})
    public String listBooks(Model model) {
        logger.info("Entering listBooks method");
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("listBooks", books);
        logger.info("Exiting listBooks method, retrieved {} books", books.size());
        return "book_list";
    }

    @GetMapping("/showNewForm")
    @Secured("ROLE_ADMIN")
    public String showNewBookForm(Model model) {
        logger.info("Entering showNewBookForm method");
        model.addAttribute("book", new Book());
        logger.info("Exiting showNewBookForm method");
        return "new_book";
    }

    @PostMapping("/save")
    @Secured("ROLE_ADMIN")
    public String saveBook(@ModelAttribute("book") Book book) {
        logger.info("Entering saveBook method with book details: {}", book);
        bookService.saveBook(book);
        logger.info("Exiting saveBook method, book saved successfully");
        return REDIRECT_BOOKS;
    }

    @GetMapping("/showFormForUpdate/{id}")
    @Secured("ROLE_ADMIN")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        logger.info("Entering showFormForUpdate method for book ID: {}", id);
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        logger.info("Exiting showFormForUpdate method");
        return "update_book";
    }

    @PostMapping("/update")
    @Secured("ROLE_ADMIN")
    public String updateBook(@ModelAttribute("book") Book book) {
        logger.info("Entering updateBook method with book details: {}", book);
        bookService.saveBook(book); // Assuming your saveBook handles both create and update
        logger.info("Exiting updateBook method, book updated successfully");
        return REDIRECT_BOOKS;
    }

    @GetMapping("/delete/{id}")
    @Secured("ROLE_ADMIN")
    public String deleteBook(@PathVariable(value = "id") Long id) {
        logger.warn("Entering deleteBook method for book ID: {}", id);
        bookService.deleteBookById(id);
        logger.info("Exiting deleteBook method, book deleted successfully");
        return REDIRECT_BOOKS;
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        logger.info("Entering searchBooks method with keyword: {}", keyword);
        List<Book> searchResults = bookService.searchBooks(keyword);
        model.addAttribute("listBooks", searchResults);
        logger.info("Exiting searchBooks method, found {} results for keyword: {}", searchResults.size(), keyword);
        return "book_list";
    }
}



