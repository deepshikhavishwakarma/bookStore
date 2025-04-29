package com.hcl.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.hcl.entity.Book;
import com.hcl.service.BookService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BookService bookService;
    private static final String REDIRECT_BOOKS = "redirect:/books";

    @Test
    @WithMockUser
    void listBooks_shouldReturnBookListAndBookListView() throws Exception {
    	List<Book> books = Arrays.asList(new Book("Book 1", "Author 1", 5, 10.0),
                new Book("Book 2", "Author 2", 3, 15.0));
when(bookService.getAllBooks()).thenReturn(books);

// Act & Assert
mockMvc.perform(get("/books"))
.andExpect(status().isOk())
.andExpect(model().attribute("listBooks", books))
.andExpect(view().name("book_list"));
verify(bookService, times(1)).getAllBooks();

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showNewBookForm_shouldReturnNewBookFormView() throws Exception {
    	mockMvc.perform(get("/books/showNewForm"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("book"))
        .andExpect(view().name("new_book"));

    }

    @Test
    @WithMockUser(roles = "USER")
    void showNewBookForm_asUser_shouldBeForbidden() throws Exception {
    	mockMvc.perform(get("/books/showNewForm"))
        .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "USER")
    void saveBook_asUser_shouldBeForbidden() throws Exception {
    	 Book newBook = new Book("New Book", "New Author", 2, 20.0);
         mockMvc.perform(post("/books/save")
                        .flashAttr("book", newBook))
                .andExpect(status().isForbidden());
         verify(bookService, never()).saveBook(any(Book.class));
    
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showFormForUpdate_shouldReturnUpdateBookFormViewWithBook() throws Exception {
        // Arrange
        Long bookId = 1L;
        Book existingBook = new Book();
        existingBook.setBookId(bookId);
        existingBook.setBookName("Existing Book");
        existingBook.setAuthorName("Some Author");
        existingBook.setQuantity(3);
        existingBook.setPrice(19.50);

        when(bookService.getBookById(bookId)).thenReturn(existingBook);

        // Act & Assert
        mockMvc
            .perform(get("/books/showFormForUpdate/{id}", bookId))
            .andExpect(status().isOk())
            .andExpect(model().attribute("book", existingBook))
            .andExpect(view().name("update_book"));
        verify(bookService, times(1)).getBookById(bookId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void showFormForUpdate_asUser_shouldBeForbidden() throws Exception {
    	 mockMvc.perform(get("/books/showFormForUpdate/{id}", 1L))
         .andExpect(status().isForbidden());
  verify(bookService, never()).getBookById(anyLong());

    }

	
    @Test
    @WithMockUser(roles = "USER")
    void updateBook_asUser_shouldBeForbidden() throws Exception {
    	 Book updatedBook = new Book("Updated Book", "Updated Author", 7, 22.0);
         mockMvc.perform(post("/books/update")
                        .flashAttr("book", updatedBook))
                .andExpect(status().isForbidden());
         verify(bookService, never()).saveBook(any(Book.class));
     
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_shouldDeleteBookAndRedirectToList() throws Exception {
    	 Long bookIdToDelete = 1L;
         mockMvc.perform(get("/books/delete/{id}", bookIdToDelete))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
         verify(bookService, times(1)).deleteBookById(bookIdToDelete);
    
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBook_asUser_shouldBeForbidden() throws Exception {
    	 mockMvc.perform(get("/books/delete/{id}", 1L))
         .andExpect(status().isForbidden());
  verify(bookService, never()).deleteBookById(anyLong());

    }

    @Test
    @WithMockUser
    void searchBooks_shouldReturnSearchResultsAndBookListView() throws Exception {
    	 String keyword = "test";
         List<Book> searchResults = Arrays.asList(new Book("Test Book", "Author", 2, 10.0));
         when(bookService.searchBooks(keyword)).thenReturn(searchResults);

         mockMvc.perform(get("/books/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(model().attribute("listBooks", searchResults))
                .andExpect(view().name("book_list"));
         verify(bookService, times(1)).searchBooks(keyword);
    
    }

    @Test
    @WithMockUser
    void searchBooks_emptyKeyword_shouldReturnAllBooks() throws Exception {
    	 List<Book> allBooks = Arrays.asList(new Book("Book 1", "Author 1", 5, 10.0),
                 new Book("Book 2", "Author 2", 3, 15.0));
when(bookService.searchBooks("")).thenReturn(allBooks);

mockMvc.perform(get("/books/search")
.param("keyword", ""))
.andExpect(status().isOk())
.andExpect(model().attribute("listBooks", allBooks))
.andExpect(view().name("book_list"));
verify(bookService, times(1)).searchBooks("");

    }
}