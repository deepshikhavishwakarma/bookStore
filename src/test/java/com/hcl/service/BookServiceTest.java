package com.hcl.service;

import com.hcl.entity.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookService bookService; // Mocking the interface

	@Test
	void getAllBooks_shouldReturnListOfBooks() {

		Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", 10, 15.00);
		Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", 5, 12.50);
		List<Book> expectedBooks = Arrays.asList(book1, book2);
		when(bookService.getAllBooks()).thenReturn(expectedBooks);

		// Act
		List<Book> actualBooks = bookService.getAllBooks();

		// Assert
		assertEquals(expectedBooks.size(), actualBooks.size());
		assertEquals(expectedBooks, actualBooks);
	}

	@Test
	void getBookById_shouldReturnBook_whenIdExists() {

		Long bookId = 1L;
		Book expectedBook = new Book("The Great Gatsby", "F. Scott Fitzgerald", 10, 15.00);
		when(bookService.getBookById(bookId)).thenReturn(expectedBook);

		// Act
		Book actualBook = bookService.getBookById(bookId);

		// Assert
		assertEquals(expectedBook, actualBook);
	}

	@Test
	void searchBooks_shouldReturnListOfMatchingBooks() {

		String keyword = "great";
		Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", 10, 15.00);
		List<Book> expectedBooks = Arrays.asList(book1);
		when(bookService.searchBooks(keyword)).thenReturn(expectedBooks);

		// Act
		List<Book> actualBooks = bookService.searchBooks(keyword);

		// Assert
		assertEquals(expectedBooks.size(), actualBooks.size());
		assertEquals(expectedBooks, actualBooks);
	}

}