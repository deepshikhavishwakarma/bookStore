package com.hcl.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hcl.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcl.entity.Book;
import com.hcl.exception.BookNotFoundException;
import com.hcl.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<Book> getAllBooks() {
        logger.info("Getting all available books");
        return bookRepository.findAll().stream().filter(book -> book.getQuantity() > 0).collect(Collectors.toList());
    }

    @Override
    public void saveBook(Book book) {
        logger.info("Saving book");
        this.bookRepository.save(book);
    }

    @Override
    public Book getBookById(Long id) {
        logger.info("Getting book by ID: {}", id);
        Optional<Book> optional = bookRepository.findById(id);
        return optional.orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteBookById(Long id) {
        logger.info("Deleting book by ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + id));
        cartItemRepository.deleteByBook_BookId(id);
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        logger.info("Searching books with keyword: {}", keyword);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return bookRepository.searchBooks(keyword.toLowerCase());
        }
        return getAllBooks();
    }
}


