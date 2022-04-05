package com.book.management.controller;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.request.FindAllBooksFromAuthorRequest;
import com.book.management.dto.request.UpdateBookRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class)
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
                .bookId(1)
                .isbn(9780062315007L)
                .bookTitle("The Alchemist")
                .bookAuthor("Paulo Coelho")
                .build();
    }

    //test getBook method when success
    @Test
    void getBook_success() throws Exception {
        //simulate data response for return object
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(BookResponse.builder()
                        .bookId(bookModel.getBookId())
                        .isbn(bookModel.getIsbn())
                        .bookTitle(bookModel.getBookTitle())
                        .bookAuthor(bookModel.getBookAuthor())
                        .build())
                .build();

        //given
        when(bookService.getBook(1)).thenReturn(dataResponse);

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

        verify(bookService, times(1)).getBook(1);
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
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

        //simulate data response for return object
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
                .content(mapper.writeValueAsString(addBookRequest));

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
                .content(mapper.writeValueAsString(addBookRequest));

        MockHttpServletResponse response = mockMvc.perform(mockRequest)
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        //then
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertTrue(response.getContentAsString().isEmpty());

    }

    //test addBook method when fail because book already exist
    @Test
    void addBook_failBookAlreadyExist() throws Exception {
        //create add book request object
        AddBookRequest addBookRequest = AddBookRequest.builder()
                .isbn(bookModel.getIsbn())
                .bookTitle(bookModel.getBookTitle())
                .bookAuthor(bookModel.getBookAuthor())
                .build();

        //simulate data response for return object
        String message = "Book with isbn " + addBookRequest.getIsbn() + " already exist";
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(message)
                .build();

        //given
        when(bookService.addBook(addBookRequest)).thenReturn(dataResponse);

        //when
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/book/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addBookRequest));

        MockHttpServletResponse response = mockMvc.perform(mockRequest)

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(message))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).addBook(addBookRequest);
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());

    }


    //test getAllBooks method when success
    @Test
    void getAllBooks_success() throws Exception {
        Book secondBook = Book.builder()
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .bookId(2)
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();
        List<Book> bookList = List.of(bookModel, secondBook);
        List<BookResponse> bookResponseList = new ArrayList<>();
        bookList.forEach(
                data -> bookResponseList.add(
                        BookResponse.builder()
                                .bookId(data.getBookId())
                                .isbn(data.getIsbn())
                                .bookTitle(data.getBookTitle())
                                .bookAuthor(data.getBookAuthor())
                                .build())
        );
        //simulate data response for return object
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookResponseList)
                .build();

        //given
        when(bookService.getAllBooks()).thenReturn(dataResponse);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/list")
                        .contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[1].bookId").value(secondBook.getBookId()))
                .andExpect(jsonPath("$.data[1].isbn").value(secondBook.getIsbn()))
                .andExpect(jsonPath("$.data[1].bookTitle").value(secondBook.getBookTitle()))
                .andExpect(jsonPath("$.data[1].bookAuthor").value(secondBook.getBookAuthor()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).getAllBooks();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertFalse(response.getContentAsString().isEmpty());

    }

    //test getAllBooks method when no record found
    @Test
    void getAllBooks_noRecord() throws Exception {
        List<BookResponse> bookList = new ArrayList<>();

        //simulate data response for return object
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

    }

    //test updateBook method when success
    @Test
    void updateBook_success() throws Exception {
        //create update book request object
        UpdateBookRequest updateBookRequest = UpdateBookRequest.builder()
                .bookId(1)
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();

        //simulate data response for return object
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(BookResponse.builder()
                        .bookId(updateBookRequest.getBookId())
                        .isbn(updateBookRequest.getIsbn())
                        .bookTitle(updateBookRequest.getBookTitle())
                        .bookAuthor(updateBookRequest.getBookAuthor())
                        .build())
                .build();

        //given
        when(bookService.updateBook(updateBookRequest)).thenReturn(dataResponse);

        //when
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/book/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateBookRequest));

        MockHttpServletResponse response = mockMvc.perform(mockRequest)

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookId").value(updateBookRequest.getBookId()))
                .andExpect(jsonPath("$.data.isbn").value(updateBookRequest.getIsbn()))
                .andExpect(jsonPath("$.data.bookTitle").value(updateBookRequest.getBookTitle()))
                .andExpect(jsonPath("$.data.bookAuthor").value(updateBookRequest.getBookAuthor()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).updateBook(updateBookRequest);
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertFalse(response.getContentAsString().isEmpty());
    }

    //test updateBook method when book not found
    @Test
    void updateBook_failBookNotFound() throws Exception {
        //create update book request object
        UpdateBookRequest updateBookRequest = UpdateBookRequest.builder()
                .bookId(100)
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();

        //given
        when(bookService.updateBook(updateBookRequest))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/book/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateBookRequest));

        MockHttpServletResponse response = mockMvc.perform(mockRequest)
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        //then
        verify(bookService, times(1)).updateBook(updateBookRequest);
        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());
        assertTrue(response.getContentAsString().isEmpty());
    }

    //test deleteBook method when success
    @Test
    void deleteBook_success() throws Exception {
        //simulate data response for return object
        String bookResponse = "Successfully Delete Book with bookId " + bookModel.getBookId();
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookResponse)
                .build();

        //given
        when(bookService.deleteBook(bookModel.getBookId())).thenReturn(dataResponse);

        //when
        MockHttpServletResponse response = mockMvc.perform(delete("/api/book/delete/{bookId}", bookModel.getBookId())
                        .contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(bookResponse))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).deleteBook(bookModel.getBookId());
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertFalse(response.getContentAsString().isEmpty());

    }

    //test deleteBook method when fail
    @Test
    void deleteBook_bookNotFound() throws Exception {
        //given
        when(bookService.deleteBook(100)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        MockHttpServletResponse response = mockMvc.perform(delete("/api/book/delete/{bookId}", 100)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        //then
        verify(bookService, times(1)).deleteBook(100);
        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());
        assertTrue(response.getContentAsString().isEmpty());

    }

    //test findAllBooksFromAuthor method when success
    @Test
    void findAllBooksFromAuthor_success() throws Exception {
        //create findAllBooksFromAuthorRequest object
        FindAllBooksFromAuthorRequest findAllBooksFromAuthorRequest = FindAllBooksFromAuthorRequest.builder()
                .bookAuthor(bookModel.getBookAuthor())
                .build();

        Book secondBook = Book.builder()
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .bookId(2)
                .isbn(9780525432791L)
                .bookTitle("The Spy")
                .bookAuthor("Paulo Coelho")
                .build();
        List<Book> bookList = List.of(bookModel, secondBook);
        List<BookResponse> bookResponseList = new ArrayList<>();
        bookList.forEach(
                data -> bookResponseList.add(
                        BookResponse.builder()
                                .bookId(data.getBookId())
                                .isbn(data.getIsbn())
                                .bookTitle(data.getBookTitle())
                                .bookAuthor(data.getBookAuthor())
                                .build())
        );

        //simulate data response for return object
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookResponseList)
                .build();

        //given
        when(bookService.findAllBooksFromAuthor(findAllBooksFromAuthorRequest)).thenReturn(dataResponse);

        //when
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/book/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(findAllBooksFromAuthorRequest));
        MockHttpServletResponse response = mockMvc.perform(mockRequest)

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[1].bookId").value(secondBook.getBookId()))
                .andExpect(jsonPath("$.data[1].isbn").value(secondBook.getIsbn()))
                .andExpect(jsonPath("$.data[1].bookTitle").value(secondBook.getBookTitle()))
                .andExpect(jsonPath("$.data[1].bookAuthor").value(secondBook.getBookAuthor()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).findAllBooksFromAuthor(findAllBooksFromAuthorRequest);
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertFalse(response.getContentAsString().isEmpty());

    }

    //test findAllBooksFromAuthor method when no record
    @Test
    void findAllBooksFromAuthor_noRecord() throws Exception {
        //create findAllBooksFromAuthorRequest object
        FindAllBooksFromAuthorRequest findAllBooksFromAuthorRequest = FindAllBooksFromAuthorRequest.builder()
                .bookAuthor("John Doe")
                .build();
        List<BookResponse> bookList = new ArrayList<>();

        //simulate data response for return object
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookList)
                .build();

        //given
        when(bookService.findAllBooksFromAuthor(findAllBooksFromAuthorRequest)).thenReturn(dataResponse);

        //when
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/book/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(findAllBooksFromAuthorRequest));
        MockHttpServletResponse response = mockMvc.perform(mockRequest)

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).findAllBooksFromAuthor(findAllBooksFromAuthorRequest);
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());

    }

    //test findAllBooksOrderByIsbn method when success
    @Test
    void findAllBooksOrderByIsbn_success() throws Exception {
        Book secondBook = Book.builder()
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .bookId(2)
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();
        List<Book> bookList = List.of(secondBook, bookModel);
        List<BookResponse> bookResponseList = new ArrayList<>();
        bookList.forEach(
                data -> bookResponseList.add(
                        BookResponse.builder()
                                .bookId(data.getBookId())
                                .isbn(data.getIsbn())
                                .bookTitle(data.getBookTitle())
                                .bookAuthor(data.getBookAuthor())
                                .build())
        );

        //simulate data response for return object
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookResponseList)
                .build();

        //given
        when(bookService.findAllBooksOrderByIsbn()).thenReturn(dataResponse);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/ordered-isbn"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bookId").value(secondBook.getBookId()))
                .andExpect(jsonPath("$.data[0].isbn").value(secondBook.getIsbn()))
                .andExpect(jsonPath("$.data[0].bookTitle").value(secondBook.getBookTitle()))
                .andExpect(jsonPath("$.data[0].bookAuthor").value(secondBook.getBookAuthor()))
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).findAllBooksOrderByIsbn();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());
        assertFalse(response.getContentAsString().isEmpty());

    }

    //test findAllBooksOrderByIsbn method when no record
    @Test
    void findAllBooksOrderByIsbn_noRecord() throws Exception {
        List<BookResponse> bookList = new ArrayList<>();

        //simulate data response for return object
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(bookList)
                .build();

        //given
        when(bookService.findAllBooksOrderByIsbn()).thenReturn(dataResponse);

        //when
        MockHttpServletResponse response = mockMvc.perform(get("/api/book/ordered-isbn"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn().getResponse();

        verify(bookService, times(1)).findAllBooksOrderByIsbn();
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON.toString());

    }




}