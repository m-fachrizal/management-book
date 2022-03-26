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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
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
                123456L,
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


    @Test
    void whenInvalidBookId_thenShouldReturn404() throws Exception {
        //given
        when(bookService.getBook(3)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //DataResponse<Object> response = bookService.getBook(3);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/id/{bookId}", 3)
                //.andExpect(status().is4xxClientError());
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        //assertEquals(builder, response);

        //then
        verify(bookService, times(1)).getBook(3);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();

    }






}