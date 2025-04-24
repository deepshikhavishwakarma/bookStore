package com.hcl.service;


import java.util.List;

import com.hcl.entity.Book;

public interface BookService {
    List<Book> getAllBooks();
    void saveBook(Book book);
    Book getBookById(Long id);
    void deleteBookById(Long id);
    List<Book> searchBooks(String keyword);
}