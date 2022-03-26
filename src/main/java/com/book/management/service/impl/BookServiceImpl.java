package com.book.management.service.impl;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.response.BookResponse;
import com.book.management.dto.request.UpdateBookRequest;
import com.book.management.dto.response.DataResponse;
import com.book.management.entity.Book;
import com.book.management.repository.BookRepository;
import com.book.management.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class BookServiceImpl implements BookService {

  @Autowired
  private BookRepository bookRepository;

  @Override
  public DataResponse<?> addBook(AddBookRequest addBookRequest){
    Book bookModel = Book.builder()
            .isbn(addBookRequest.getIsbn())
            .bookTitle(addBookRequest.getBookTitle())
            .bookAuthor(addBookRequest.getBookAuthor())
            .build();

    Book addBook = bookRepository.save(bookModel);

    BookResponse bookResponse = BookResponse.builder()
            .bookId(addBook.getBookId())
            .isbn(addBook.getIsbn())
            .bookTitle(addBook.getBookTitle())
            .bookAuthor(addBook.getBookAuthor())
            .build();

    log.info("Book added successfully");

    return DataResponse.builder()
            .data(bookResponse)
            .build();
  }

  @Override
  public DataResponse<?> getAllBooks() {
    Iterable<Book> bookModel = bookRepository.findAll();
    List<BookResponse> bookResponses = new ArrayList<>();
    bookModel.forEach(
            data -> bookResponses.add(BookResponse.builder()
                            .bookId(data.getBookId())
                            .isbn(data.getIsbn())
                            .bookTitle(data.getBookTitle())
                            .bookAuthor(data.getBookAuthor())
                            .build())
    );

    long test = bookModel.spliterator().getExactSizeIfKnown();
    int test2 = bookResponses.size();
    log.info("bookModel size: " + test);
    log.info("bookResponse size: " + test2);

    //if(bookResponses.isEmpty()){
      //String message = "There is no book right now.";
      //return DataResponse.builder()
      //              .data(message)
      //              .build();
    //} else {
      log.info("All Book record found");
      return DataResponse.builder()
              .data(bookResponses)
              .build();
    //}
  }

  @Override
  public DataResponse<Object> getBook(Integer id) {
    Book bookModel = bookRepository.findById(id).orElseThrow(() ->
      new ResponseStatusException(HttpStatus.NOT_FOUND));
    log.info("Book record with bookId {} found", id);
    BookResponse bookResponse = BookResponse.builder()
            .bookId(bookModel.getBookId())
            .isbn(bookModel.getIsbn())
            .bookTitle(bookModel.getBookTitle())
            .bookAuthor(bookModel.getBookAuthor())
            .build();
    return DataResponse.builder()
            .data(bookResponse)
            .build();
  }

  @Override
  public DataResponse<?> updateBook(UpdateBookRequest updateBookRequest){
    Book bookModel = bookRepository.findById(updateBookRequest.getBookId()).orElseThrow(() ->
      new ResponseStatusException(HttpStatus.NOT_FOUND));

    bookModel.setBookId(updateBookRequest.getBookId());
    bookModel.setIsbn(updateBookRequest.getIsbn());
    bookModel.setBookTitle(updateBookRequest.getBookTitle());
    bookModel.setBookAuthor(updateBookRequest.getBookAuthor());

    Book updateBook = bookRepository.save(bookModel);

    BookResponse bookResponse = BookResponse.builder()
            .bookId(updateBook.getBookId())
            .isbn(updateBook.getIsbn())
            .bookTitle(updateBook.getBookTitle())
            .bookAuthor(updateBook.getBookAuthor())
            .build();

    log.info("Book record with bookId {} found and updated !", updateBookRequest.getBookId());

    return DataResponse.builder()
            .data(bookResponse)
            .build();
  }

  @Override
  public DataResponse<?> deleteBook(Integer bookId) {
    Book bookModel = bookRepository.findById(bookId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND));

    bookRepository.delete(bookModel);
    String bookResponse = "Successfully Delete Book with bookId " + bookId;
    log.info("Successfully delete book with bookId {}", bookId);
    return DataResponse.builder()
            .data(bookResponse)
            .build();
  }

}
