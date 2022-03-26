package com.book.management.service.impl;

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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
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

    @BeforeEach
    void setUp() {
        bookModel = new Book(
                LocalDateTime.now(),
                LocalDateTime.now(),
                2,
                123456L,
                "The Alchemist",
                "Paulo Coelho");
    }

    @Test
    void whenValidBookId_thenBookShouldBeFound() {
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
        assertThat(response).isNotNull();
        assertEquals(response, dataBook);

        //System.out.println(response);
        log.info(response.toString());
    }


    @Test
    void whenInvalidBookId_thenShouldReturn404() {
        String message = null;

        //given
        when(bookRepository.findById(3)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
                //Optional.of(new Book()));

        //when
        try{
            DataResponse<Object> response = bookServiceImpl.getBook(3);
        } catch (Exception e) {
            message = e.getMessage();
        }

        //then
        verify(bookRepository, times(1)).findById(3);
        assertEquals(message, "404 NOT_FOUND");
        //assertThrows()
        //assertEquals(response, new ResponseStatusException(HttpStatus.NOT_FOUND));
                //.isNull();
        //assertFalse(response.toString().isEmpty());

        //System.out.println(response);
        //log.info(response.toString());
    }

}