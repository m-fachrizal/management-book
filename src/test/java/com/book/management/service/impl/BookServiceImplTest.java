package com.book.management.service.impl;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.response.BookResponse;
import com.book.management.dto.response.DataResponse;
import com.book.management.entity.Book;
import com.book.management.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookServiceImpl bookServiceImpl;

    Book bookModel;
    String message = null;

    @BeforeEach
    void setUp() {
        bookModel = Book.builder()
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .bookId(2)
                .isbn(9780062315007L)
                .bookTitle("The Alchemist")
                .bookAuthor("Paulo Coelho")
                .build();
                /*
                new Book(
                LocalDateTime.now(),
                LocalDateTime.now(),
                2,
                9780062315007L,
                "The Alchemist",
                "Paulo Coelho");
                 */
    }

    //test getBook method when success
    @Test
    void getBook_success() {
        DataResponse<Object> dataBook = DataResponse.builder()
                .data(BookResponse.builder()
                        .bookId(bookModel.getBookId())
                        .isbn(bookModel.getIsbn())
                        .bookTitle(bookModel.getBookTitle())
                        .bookAuthor(bookModel.getBookAuthor())
                        .build())
                .build();

        //given
        when(bookRepository.findById(2)).thenReturn(Optional.of(bookModel));

        //when
        DataResponse<Object> response = bookServiceImpl.getBook(2);

        //then
        verify(bookRepository, times(1)).findById(2);
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataBook);

    }

    //test getBook method when fail
    @Test
    void getBook_bookNotFound() {

        //given
        when(bookRepository.findById(3)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        try{
            DataResponse<Object> response = bookServiceImpl.getBook(3);
        } catch (Exception e) {
            message = e.getMessage();
        }

        //then
        verify(bookRepository, times(1)).findById(3);
        assertEquals(message, HttpStatus.NOT_FOUND.toString());
    }

    //test addBook method when success
    @Test
    void addBook_success() {
        //create add book request object
        AddBookRequest addBookRequest = AddBookRequest.builder()
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();

        //simulate book object before calling save method from repository
        bookModel = Book.builder()
                .isbn(addBookRequest.getIsbn())
                .bookTitle(addBookRequest.getBookTitle())
                .bookAuthor(addBookRequest.getBookAuthor())
                .build();
        //simulate book object after calling save method from repository
        Book secondBook = Book.builder()
                .bookId(3)
                .isbn(bookModel.getIsbn())
                .bookTitle(bookModel.getBookTitle())
                .bookAuthor(bookModel.getBookAuthor())
                .build();

        //simulate data response for return object to be compared
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(BookResponse.builder()
                        .bookId(3)
                        .isbn(secondBook.getIsbn())
                        .bookTitle(secondBook.getBookTitle())
                        .bookAuthor(secondBook.getBookAuthor())
                        .build())
                .build();

        //given
        when(bookRepository.save(bookModel)).thenReturn(secondBook);

        //when
        DataResponse<Object> response = bookServiceImpl.addBook(addBookRequest);

        //then
        verify(bookRepository, times(1)).save(bookModel);
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataResponse);
    }

    //test addBook method when fail because of bad request
    @Test
    void addBook_failBadRequest() {
        //create empty addBookRequest object
        AddBookRequest addBookRequest = AddBookRequest.builder().build();

        //simulate book object before calling save method from repository
        bookModel = Book.builder()
                .isbn(addBookRequest.getIsbn())
                .bookTitle(addBookRequest.getBookTitle())
                .bookAuthor(addBookRequest.getBookAuthor())
                .build();

        //given
        when(bookRepository.save(bookModel)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        //when
        try{
            DataResponse<Object> response = bookServiceImpl.addBook(addBookRequest);
        } catch (Exception e) {
            message = e.getMessage();
        }

        //then
        verify(bookRepository, times(1)).save(bookModel);
        assertEquals(message, HttpStatus.BAD_REQUEST.toString());

    }

    //test getAllBooks method when success
    @Test
    void getAllBooks_success() {
        //simulate result after calling findAll method from repository
        Iterable<Book> bookIterable = List.of(bookModel);

        List<BookResponse> bookList = new ArrayList<>();
        bookIterable.forEach(
                data -> bookList.add(
                        BookResponse.builder()
                                .bookId(data.getBookId())
                                .isbn(data.getIsbn())
                                .bookTitle(data.getBookTitle())
                                .bookAuthor(data.getBookAuthor())
                                .build())
                );

        //simulate data response for return object to be compared
        DataResponse<Object> dataBook = DataResponse.builder()
                .data(bookList)
                .build();

        //given
        when(bookRepository.findAll()).thenReturn(bookIterable);

        //when
        DataResponse<Object> response = bookServiceImpl.getAllBooks();

        //then
        verify(bookRepository, times(1)).findAll();
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataBook);
    }

    //test getAllBooks method when no record found
    @Test
    void getAllBooks_noRecord() {
        //simulate result after calling findAll method from repository
        Iterable<Book> bookIterable = List.of();

        //simulate data response for return object to be compared
        DataResponse<Object> dataBook = DataResponse.builder().data(bookIterable).build();
        log.info("dataBook: " + dataBook);

        //given
        when(bookRepository.findAll()).thenReturn(bookIterable);

        //when
        DataResponse<Object> response = bookServiceImpl.getAllBooks();
        log.info("response: " + response);

        //then
        verify(bookRepository, times(1)).findAll();
        assertEquals(response, dataBook);
    }


}