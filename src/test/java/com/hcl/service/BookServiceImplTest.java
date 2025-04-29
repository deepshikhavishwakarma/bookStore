package com.hcl.service;

import com.hcl.entity.Book;
import com.hcl.exception.BookNotFoundException;
import com.hcl.repository.BookRepository;
import com.hcl.repository.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private CartItemRepository cartItemRepository;

	@InjectMocks
	private BookServiceImpl bookService;

	@Test
	void getAllBooks_shouldReturnOnlyAvailableBooks() {
		Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", 10, 15.00);
		Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", 0, 12.50); // Quantity 0
		Book book3 = new Book("Pride and Prejudice", "Jane Austen", 5, 11.00);
		List<Book> allBooks = Arrays.asList(book1, book2, book3);
		when(bookRepository.findAll()).thenReturn(allBooks);

		// Act
		List<Book> availableBooks = bookService.getAllBooks();

		// Assert
		assertEquals(2, availableBooks.size());
		assertTrue(availableBooks.contains(book1));
		assertFalse(availableBooks.contains(book2));
		assertTrue(availableBooks.contains(book3));
		verify(bookRepository, times(1)).findAll();
	}

	@Test
	void saveBook_shouldCallBookRepositorySave() {
		Book book = new Book("New Book", "Author", 5, 20.00);

		// Act
		bookService.saveBook(book);

		// Assert
		verify(bookRepository, times(1)).save(book);
	}

	@Test
	void getBookById_shouldReturnBook_whenIdExists() {

		Long bookId = 1L;
		Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald", 10, 15.00);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		// Act
		Book retrievedBook = bookService.getBookById(bookId);

		// Assert
		assertEquals(book, retrievedBook);
		verify(bookRepository, times(1)).findById(bookId);
	}

	@Test
	void getBookById_shouldThrowBookNotFoundException_whenIdDoesNotExist() {

		Long bookId = 1L;
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Act and Assert
		assertThrows(BookNotFoundException.class, () -> bookService.getBookById(bookId));
		verify(bookRepository, times(1)).findById(bookId);
	}

	@Test
	void deleteBookById_shouldDeleteBookAndRelatedCartItems_whenIdExists() {

		Long bookId = 1L;
		Book bookToDelete = new Book("ToDelete", "Author", 2, 10.00);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookToDelete));
		doNothing().when(bookRepository).deleteById(bookId);
		doNothing().when(cartItemRepository).deleteByBookBookId(bookId);

		// Act
		bookService.deleteBookById(bookId);

		// Assert
		verify(bookRepository, times(1)).findById(bookId);
		verify(cartItemRepository, times(1)).deleteByBookBookId(bookId);
		verify(bookRepository, times(1)).deleteById(bookId);
	}

	@Test
	void deleteBookById_shouldThrowBookNotFoundException_whenIdDoesNotExist() {

		Long bookId = 1L;
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Act and Assert
		assertThrows(BookNotFoundException.class, () -> bookService.deleteBookById(bookId));
		verify(bookRepository, times(1)).findById(bookId);
		verify(cartItemRepository, never()).deleteByBookBookId(anyLong());
		verify(bookRepository, never()).deleteById(anyLong());
	}

	@Test
	void searchBooks_shouldReturnSearchResults_whenKeywordIsProvided() {

		String keyword = "great";
		Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", 10, 15.00);
		Book book2 = new Book("Some Other Book", "Another Author", 5, 12.00);
		List<Book> searchResults = Arrays.asList(book1);
		when(bookRepository.searchBooks(keyword.toLowerCase())).thenReturn(searchResults);

		// Act
		List<Book> result = bookService.searchBooks(keyword);

		// Assert
		assertEquals(1, result.size());
		assertTrue(result.contains(book1));
		verify(bookRepository, times(1)).searchBooks(keyword.toLowerCase());
		verify(bookRepository, never()).findAll();
	}

	@Test
	void searchBooks_shouldReturnAllAvailableBooks_whenKeywordIsNull() {

		Book book1 = new Book("Book 1", "Author 1", 2, 10.00);
		Book book2 = new Book("Book 2", "Author 2", 5, 15.00);
		List<Book> allAvailableBooks = Arrays.asList(book1, book2);
		when(bookRepository.findAll())
				.thenReturn(Arrays.asList(book1, book2, new Book("Out of Stock", "Someone", 0, 5.00)));

		// Act
		List<Book> result = bookService.searchBooks(null);

		// Assert
		assertEquals(2, result.size());
		assertTrue(result.contains(book1));
		assertTrue(result.contains(book2));
		verify(bookRepository, never()).searchBooks(any());
		verify(bookRepository, times(1)).findAll();
	}

	@Test
	void searchBooks_shouldReturnAllAvailableBooks_whenKeywordIsBlank() {

		Book book1 = new Book("Book A", "Writer A", 3, 8.00);
		Book book2 = new Book("Book B", "Writer B", 7, 12.00);
		List<Book> allAvailableBooks = Arrays.asList(book1, book2);
		when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, new Book("Sold Out", "Nobody", 0, 2.00)));

		// Act
		List<Book> result = bookService.searchBooks(" ");

		// Assert
		assertEquals(2, result.size());
		assertTrue(result.contains(book1));
		assertTrue(result.contains(book2));
		verify(bookRepository, never()).searchBooks(any());
		verify(bookRepository, times(1)).findAll();
	}
}