package com.book.management.controller;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.response.BookResponse;
import com.book.management.dto.response.DataResponse;
import com.book.management.entity.Book;
import com.book.management.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    ObjectMapper mapper;

    Book bookModel;

    @BeforeEach
    void setUp() {
        //given
        bookModel = Book.builder()
                .createdDate(LocalDateTime.parse("2022-01-02T14:06:59.972177"))
                .updatedDate(LocalDateTime.parse("2022-01-02T14:06:59.972177"))
                .bookId(2)
                .isbn(9780062315007L)
                .bookTitle("The Alchemist")
                .bookAuthor("Paulo Coelho")
                .build();
                /*
                new Book(
                //LocalDateTime.now(),
                LocalDateTime.parse("2022-01-02T14:06:59.972177"),
                LocalDateTime.parse("2022-01-02T14:06:59.972177"),
                2,
                123456L,
                "The Alchemist",
                "Paulo Coelho");

                 */
    }

    //test getBook method when success
    @Test
    void getBook_success() throws Exception {
        //simulate data response for return object to be compared
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(BookResponse.builder()
                        .bookId(bookModel.getBookId())
                        .isbn(bookModel.getIsbn())
                        .bookTitle(bookModel.getBookTitle())
                        .bookAuthor(bookModel.getBookAuthor())
                        .build())
                .build();

        //given
        when(bookService.getBook(2)).thenReturn(dataResponse);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/id/{bookId}", bookModel.getBookId())
                        .contentType(MediaType.APPLICATION_JSON))

        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookId").value(bookModel.getBookId()))
                .andExpect(jsonPath("$.data.isbn").value(bookModel.getIsbn()))
                .andExpect(jsonPath("$.data.bookTitle").value(bookModel.getBookTitle()))
                .andExpect(jsonPath("$.data.bookAuthor").value(bookModel.getBookAuthor()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).getBook(2);
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertNull(response.getErrorMessage());
        assertFalse(response.getContentAsString().isEmpty());

    }

    //test getBook method when fail
    @Test
    void getBook_bookNotFound() throws Exception {
        //given
        when(bookService.getBook(3)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/id/{bookId}", 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        //then
        verify(bookService, times(1)).getBook(3);
        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());
        assertTrue(response.getContentAsString().isEmpty());

    }

    //test addBook method when success
    @Test
    void addBook_success() throws Exception {
        //create add book request object
        AddBookRequest addBookRequest = AddBookRequest.builder()
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();

        //simulate data response for return object to be compared
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(BookResponse.builder()
                        .bookId(3)
                        .isbn(addBookRequest.getIsbn())
                        .bookTitle(addBookRequest.getBookTitle())
                        .bookAuthor(addBookRequest.getBookAuthor())
                        .build())
                .build();

        //given
        when(bookService.addBook(addBookRequest)).thenReturn(dataResponse);

        //when
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/book/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(addBookRequest));

        MockHttpServletResponse response = mockMvc.perform(mockRequest)

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookId").value(3))
                .andExpect(jsonPath("$.data.isbn").value(addBookRequest.getIsbn()))
                .andExpect(jsonPath("$.data.bookTitle").value(addBookRequest.getBookTitle()))
                .andExpect(jsonPath("$.data.bookAuthor").value(addBookRequest.getBookAuthor()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).addBook(addBookRequest);
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertNull(response.getErrorMessage());
        assertFalse(response.getContentAsString().isEmpty());

    }

    //test addBook method when fail because of bad request
    @Test
    void addBook_failBadRequest() throws Exception {
        //create empty addBookRequest object
        AddBookRequest addBookRequest = AddBookRequest.builder().build();

        //when
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/book/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(addBookRequest));

        MockHttpServletResponse response = mockMvc.perform(mockRequest)
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        //then
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertTrue(response.getContentAsString().isEmpty());

    }

    //test getAllBooks method when success
    @Test
    void getAllBooks_success() throws Exception {
        List<BookResponse> bookList = new ArrayList<>();
        bookList.add(BookResponse.builder()
                .bookId(bookModel.getBookId())
                .isbn(bookModel.getIsbn())
                .bookTitle(bookModel.getBookTitle())
                .bookAuthor(bookModel.getBookAuthor())
                .build());
        //simulate data response for return object to be compared
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookList)
                .build();

        //given
        when(bookService.getAllBooks()).thenReturn(dataResponse);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/list")
                        .contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bookId").value(bookModel.getBookId()))
                .andExpect(jsonPath("$.data[0].isbn").value(bookModel.getIsbn()))
                .andExpect(jsonPath("$.data[0].bookTitle").value(bookModel.getBookTitle()))
                .andExpect(jsonPath("$.data[0].bookAuthor").value(bookModel.getBookAuthor()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).getAllBooks();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertNull(response.getErrorMessage());
        assertFalse(response.getContentAsString().isEmpty());

    }

    //test getAllBooks method when no record found
    @Test
    void getAllBooks_noRecord() throws Exception {
        List<BookResponse> bookList = new ArrayList<>();

        //simulate data response for return object to be compared
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookList)
                .build();

        //given
        when(bookService.getAllBooks()).thenReturn(dataResponse);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/list")
                        .contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).getAllBooks();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertNull(response.getErrorMessage());

    }


}