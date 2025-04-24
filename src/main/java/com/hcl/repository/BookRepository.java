package com.hcl.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hcl.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	// Method to find books by name (case-insensitive and containing the keyword)
    List<Book> findByBookNameContainingIgnoreCase(String keyword);

    // Method to find books by author name (case-insensitive and containing the keyword)
    List<Book> findByAuthorNameContainingIgnoreCase(String keyword);

    // Combined search using a custom query (more flexible)
    @Query("SELECT b FROM Book b WHERE LOWER(b.bookName) LIKE %:keyword% OR LOWER(b.authorName) LIKE %:keyword%")
    List<Book> searchBooks(@Param("keyword") String keyword);


}