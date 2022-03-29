package com.book.management.service.impl;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.request.FindAllBooksFromAuthorRequest;
import com.book.management.dto.request.UpdateBookRequest;
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

import java.time.LocalDateTime;
import java.util.*;

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
                .bookId(1)
                .isbn(9780062315007L)
                .bookTitle("The Alchemist")
                .bookAuthor("Paulo Coelho")
                .build();
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
        when(bookRepository.findById(bookModel.getBookId())).thenReturn(Optional.of(bookModel));

        //when
        DataResponse<Object> response = bookServiceImpl.getBook(bookModel.getBookId());

        //then
        verify(bookRepository, times(1)).findById(bookModel.getBookId());
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
            log.info(message);
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
        Book addBook = Book.builder()
                .isbn(addBookRequest.getIsbn())
                .bookTitle(addBookRequest.getBookTitle())
                .bookAuthor(addBookRequest.getBookAuthor())
                .build();
        //simulate book object after calling save method from repository
        Book savedBook = Book.builder()
                .bookId(2)
                .isbn(bookModel.getIsbn())
                .bookTitle(bookModel.getBookTitle())
                .bookAuthor(bookModel.getBookAuthor())
                .build();

        //simulate data response for return object to be compared
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data(BookResponse.builder()
                        .bookId(savedBook.getBookId())
                        .isbn(savedBook.getIsbn())
                        .bookTitle(savedBook.getBookTitle())
                        .bookAuthor(savedBook.getBookAuthor())
                        .build())
                .build();

        //given
        when(bookRepository.findByIsbn(addBookRequest.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(addBook)).thenReturn(savedBook);

        //when
        DataResponse<Object> response = bookServiceImpl.addBook(addBookRequest);

        //then
        verify(bookRepository, times(1)).save(addBook);
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataResponse);
    }

    //test addBook method when fail because of bad request
    @Test
    void addBook_failBadRequest() {
        //create empty addBookRequest object
        AddBookRequest addBookRequest = AddBookRequest.builder().build();

        //simulate book object before calling save method from repository
        Book addBook = Book.builder()
                .isbn(addBookRequest.getIsbn())
                .bookTitle(addBookRequest.getBookTitle())
                .bookAuthor(addBookRequest.getBookAuthor())
                .build();

        //given
        when(bookRepository.save(addBook)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        //when
        try{
            DataResponse<Object> response = bookServiceImpl.addBook(addBookRequest);
        } catch (Exception e) {
            message = e.getMessage();
            log.info(message);
        }

        //then
        verify(bookRepository, times(1)).save(addBook);
        assertEquals(message, HttpStatus.BAD_REQUEST.toString());

    }

    //test addBook method when fail because book already exist
    @Test
    void addBook_failBookAlreadyExist() {
        //create addBookRequest object
        AddBookRequest addBookRequest = AddBookRequest.builder()
                .isbn(bookModel.getIsbn())
                .bookTitle(bookModel.getBookTitle())
                .bookAuthor(bookModel.getBookAuthor())
                .build();

        //simulate data response for return object to be compared
        DataResponse<Object> dataResponse = DataResponse.builder()
                .data("Book with isbn " + addBookRequest.getIsbn() + " already exist")
                .build();

        //given
        when(bookRepository.findByIsbn(addBookRequest.getIsbn())).thenReturn(Optional.of(bookModel));

        //when
        DataResponse<Object> response = bookServiceImpl.addBook(addBookRequest);

        //then
        verify(bookRepository, times(1)).findByIsbn(addBookRequest.getIsbn());
        assertEquals(response, dataResponse);

    }

    //test getAllBooks method when success
    @Test
    void getAllBooks_success() {
        //simulate result after calling findAll method from repository
        Book secondBook = Book.builder()
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .bookId(2)
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();
        Iterable<Book> bookIterable = List.of(bookModel, secondBook);

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

        //given
        when(bookRepository.findAll()).thenReturn(bookIterable);

        //when
        DataResponse<Object> response = bookServiceImpl.getAllBooks();

        //then
        verify(bookRepository, times(1)).findAll();
        assertEquals(response, dataBook);
    }

    //test updateBook method when success
    @Test
    void updateBook_success() {
        //create add book request object
        UpdateBookRequest updateBookRequest = UpdateBookRequest.builder()
                .bookId(1)
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();

        //simulate book object for update
        bookModel.setIsbn(updateBookRequest.getIsbn());
        bookModel.setBookTitle(updateBookRequest.getBookTitle());
        bookModel.setBookAuthor(updateBookRequest.getBookAuthor());

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
        when(bookRepository.findById(updateBookRequest.getBookId()))
                .thenReturn(Optional.of(bookModel));
        when(bookRepository.save(bookModel)).thenReturn(bookModel);

        //when
        DataResponse<Object> response = bookServiceImpl.updateBook(updateBookRequest);

        //then
        verify(bookRepository, times(1)).findById(updateBookRequest.getBookId());
        verify(bookRepository, times(1)).save(bookModel);
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataResponse);
    }

    //test updateBook method when fail
    @Test
    void updateBook_failBookIdNotFound() {
        //create add book request object
        UpdateBookRequest updateBookRequest = UpdateBookRequest.builder()
                .bookId(100)
                .isbn(9780439708180L)
                .bookTitle("Harry Potter and the Sorcerer's Stone (#1)")
                .bookAuthor("J.K. Rowling")
                .build();

        //given
        when(bookRepository.findById(updateBookRequest.getBookId()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        try{
            DataResponse<Object> response = bookServiceImpl.updateBook(updateBookRequest);
        } catch (Exception e) {
            message = e.getMessage();
            log.info(message);
        }

        //then
        verify(bookRepository, times(1)).findById(updateBookRequest.getBookId());
        assertEquals(message, HttpStatus.NOT_FOUND.toString());
    }

    //test deleteBook method when success
    @Test
    void deleteBook_success() {
        //simulate data response for return object to be compared
        String bookResponse = "Successfully Delete Book with bookId " + bookModel.getBookId();
        DataResponse<Object> dataBook = DataResponse.builder()
                .data(bookResponse)
                .build();

        //given
        when(bookRepository.findById(bookModel.getBookId())).thenReturn(Optional.of(bookModel));

        //when
        DataResponse<Object> response = bookServiceImpl.deleteBook(bookModel.getBookId());

        //then
        verify(bookRepository, times(1)).findById(bookModel.getBookId());
        verify(bookRepository, times(1)).deleteById(bookModel.getBookId());
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataBook);

    }

    //test deleteBook method when fail
    @Test
    void deleteBook_bookNotFound() {

        //given
        when(bookRepository.findById(100)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        //when
        try{
            DataResponse<Object> response = bookServiceImpl.deleteBook(100);
        } catch (Exception e) {
            message = e.getMessage();
            log.info(message);
        }

        //then
        verify(bookRepository, times(1)).findById(100);
        assertEquals(message, HttpStatus.NOT_FOUND.toString());
    }

    //test findAllBooksFromAuthor method when success
    @Test
    void findAllBooksFromAuthor_success() {
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

        //simulate data response for return object to be compared
        DataResponse<Object> dataBook = DataResponse.builder()
                .data(bookResponseList)
                .build();

        //given
        when(bookRepository.findAllBooksFromAuthor(findAllBooksFromAuthorRequest.getBookAuthor()))
                .thenReturn(bookList);

        //when
        DataResponse<Object> response = bookServiceImpl.findAllBooksFromAuthor(findAllBooksFromAuthorRequest);
        log.info(response.toString());

        //then
        verify(bookRepository, times(1)).findAllBooksFromAuthor(findAllBooksFromAuthorRequest.getBookAuthor());
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataBook);
    }

    //test findAllBooksFromAuthor method when no record found
    @Test
    void findAllBooksFromAuthor_noRecord() {
        //create findAllBooksFromAuthorRequest object
        FindAllBooksFromAuthorRequest findAllBooksFromAuthorRequest = FindAllBooksFromAuthorRequest.builder()
                .bookAuthor("John Doe")
                .build();

        List<Book> bookList = new ArrayList<>();

        //simulate data response for return object to be compared
        DataResponse<Object> dataBook = DataResponse.builder().data(bookList).build();

        //given
        when(bookRepository.findAllBooksFromAuthor(findAllBooksFromAuthorRequest.getBookAuthor()))
                .thenReturn(bookList);

        //when
        DataResponse<Object> response = bookServiceImpl.findAllBooksFromAuthor(findAllBooksFromAuthorRequest);

        //then
        verify(bookRepository, times(1)).findAllBooksFromAuthor(findAllBooksFromAuthorRequest.getBookAuthor());
        assertEquals(response, dataBook);
    }

    //test findAllBooksOrderByIsbn method when success
    @Test
    void findAllBooksOrderByIsbn_success() {
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

        //simulate data response for return object to be compared
        DataResponse<Object> dataBook = DataResponse.builder()
                .data(bookResponseList)
                .build();

        //given
        when(bookRepository.findAllBooksOrderByIsbn())
                .thenReturn(bookList);

        //when
        DataResponse<Object> response = bookServiceImpl.findAllBooksOrderByIsbn();
        log.info(response.toString());

        //then
        verify(bookRepository, times(1)).findAllBooksOrderByIsbn();
        assertFalse(response.toString().isEmpty());
        assertEquals(response, dataBook);
    }

    //test findAllBooksOrderByIsbn method when no record found
    @Test
    void findAllBooksOrderByIsbn_noRecord() {
        List<Book> bookList = new ArrayList<>();

        //simulate data response for return object to be compared
        DataResponse<Object> dataBook = DataResponse.builder().data(bookList).build();

        //given
        when(bookRepository.findAllBooksOrderByIsbn())
                .thenReturn(bookList);

        //when
        DataResponse<Object> response = bookServiceImpl.findAllBooksOrderByIsbn();

        //then
        verify(bookRepository, times(1)).findAllBooksOrderByIsbn();
        assertEquals(response, dataBook);
    }

}