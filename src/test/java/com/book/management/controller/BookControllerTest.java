package com.book.management.controller;

import com.book.management.dto.response.BookResponse;
import com.book.management.dto.response.DataResponse;
import com.book.management.entity.Book;
import com.book.management.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class)
@ActiveProfiles("test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    Book bookModel;

    @BeforeEach
    void setUp() {
        //given
        bookModel = new Book(
                //LocalDateTime.now(),
                LocalDateTime.parse("2022-01-02T14:06:59.972177"),
                LocalDateTime.parse("2022-01-02T14:06:59.972177"),
                2,
                123456,
                "The Alchemist",
                "Paulo Coelho");
    }


    @Test
    void shouldGetABook() throws Exception {
        DataResponse<Object> builder = DataResponse.builder().data(bookModel).build();

        //given
        when(bookService.getBook(2)).thenReturn(builder);

        //when
        DataResponse<Object> response = bookService.getBook(2);
        mockMvc.perform(get("/api/book/id/{bookId}", bookModel.getBookId()))

        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createdDate").value(bookModel.getCreatedDate().toString()))
                .andExpect(jsonPath("$.data.updatedDate").value(bookModel.getUpdatedDate().toString()))
                .andExpect(jsonPath("$.data.bookId").value(bookModel.getBookId()))
                .andExpect(jsonPath("$.data.isbn").value(bookModel.getIsbn()))
                .andExpect(jsonPath("$.data.bookTitle").value(bookModel.getBookTitle()))
                .andExpect(jsonPath("$.data.bookAuthor").value(bookModel.getBookAuthor()));

        assertEquals(builder, response);
        verify(bookService, times(2)).getBook(2);

    }




}