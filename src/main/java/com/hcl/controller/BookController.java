package com.hcl.controller;

import java.util.List;

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

    @Autowired
    private BookService bookService;

    @GetMapping({"/","","/home"})
    public String listBooks(Model model) {
        model.addAttribute("listBooks", bookService.getAllBooks());
        return "book_list";
    }

    @GetMapping("/showNewForm")
    @Secured("ROLE_ADMIN")
    public String showNewBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "new_book";
    }

    @PostMapping("/save")
    @Secured("ROLE_ADMIN")
    public String saveBook(@ModelAttribute("book") Book book) {
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/showFormForUpdate/{id}")
    @Secured("ROLE_ADMIN")
    public String showFormForUpdate(@PathVariable(value = "id") Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "update_book";
    }

    @PostMapping("/update")
    @Secured("ROLE_ADMIN")
    public String updateBook(@ModelAttribute("book") Book book) {
        bookService.saveBook(book); // Assuming your saveBook handles both create and update
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    @Secured("ROLE_ADMIN")
    public String deleteBook(@PathVariable(value = "id") Long id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        List<Book> searchResults = bookService.searchBooks(keyword);
        model.addAttribute("listBooks", searchResults);
        return "book_list";
    }
}