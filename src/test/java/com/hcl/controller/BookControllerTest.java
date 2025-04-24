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
public class BookControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private BookService bookService;

    @Test
    @WithMockUser
    void listBooks_shouldReturnBookListAndBookListView() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showNewBookForm_shouldReturnNewBookFormView() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "USER")
    void showNewBookForm_asUser_shouldBeForbidden() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveBook_shouldSaveBookAndRedirectToList() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "USER")
    void saveBook_asUser_shouldBeForbidden() throws Exception {
        // ... (rest of your test method)
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
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_shouldUpdateBookAndRedirectToList() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateBook_asUser_shouldBeForbidden() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_shouldDeleteBookAndRedirectToList() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBook_asUser_shouldBeForbidden() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser
    void searchBooks_shouldReturnSearchResultsAndBookListView() throws Exception {
        // ... (rest of your test method)
    }

    @Test
    @WithMockUser
    void searchBooks_emptyKeyword_shouldReturnAllBooks() throws Exception {
        // ... (rest of your test method)
    }
}