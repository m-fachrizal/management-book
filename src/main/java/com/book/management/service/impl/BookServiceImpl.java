package com.book.management.service.impl;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.request.FindAllBooksFromAuthorRequest;
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
  public DataResponse<Object> addBook(AddBookRequest addBookRequest){
    Book checkBook = bookRepository.findByIsbn(addBookRequest.getIsbn()).orElse(null);
    if(checkBook != null){
      String message = "Book with isbn " + addBookRequest.getIsbn() + " already exist";
      log.info(message);
      return DataResponse.builder()
              .data(message)
              .build();
    } else {
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
  }

  @Override
  public DataResponse<Object> getAllBooks() {
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

    int bookTotal = bookResponses.size();
    log.info("There are {} book found", bookTotal);

    log.info("All Book record found");
    return DataResponse.builder()
            .data(bookResponses)
            .build();
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
  public DataResponse<Object> updateBook(UpdateBookRequest updateBookRequest){
    Book bookModel = bookRepository.findById(updateBookRequest.getBookId()).orElseThrow(() ->
      new ResponseStatusException(HttpStatus.NOT_FOUND));

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
  public DataResponse<Object> deleteBook(Integer bookId) {
    bookRepository.findById(bookId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND));

    bookRepository.deleteById(bookId);
    String bookResponse = "Successfully Delete Book with bookId " + bookId;
    log.info("Successfully delete book with bookId {}", bookId);
    return DataResponse.builder()
            .data(bookResponse)
            .build();
  }

  @Override
  public DataResponse<Object> findAllBooksFromAuthor(FindAllBooksFromAuthorRequest findAllBooksFromAuthorRequest) {
    List<Book> bookModel = bookRepository.findAllBooksFromAuthor(findAllBooksFromAuthorRequest.getBookAuthor());
    List<BookResponse> bookResponses = new ArrayList<>();
    bookModel.forEach(
            data -> bookResponses.add(BookResponse.builder()
                    .bookId(data.getBookId())
                    .isbn(data.getIsbn())
                    .bookTitle(data.getBookTitle())
                    .bookAuthor(data.getBookAuthor())
                    .build())
    );

    int bookTotal = bookResponses.size();
    log.info("There are {} book found from author {}", bookTotal, findAllBooksFromAuthorRequest.getBookAuthor());

    return DataResponse.builder()
            .data(bookResponses)
            .build();
  }

  @Override
  public DataResponse<Object> findAllBooksOrderByIsbn() {
    List<Book> bookModel = bookRepository.findAllBooksOrderByIsbn();
    List<BookResponse> bookResponses = new ArrayList<>();
    bookModel.forEach(
            data -> bookResponses.add(BookResponse.builder()
                    .bookId(data.getBookId())
                    .isbn(data.getIsbn())
                    .bookTitle(data.getBookTitle())
                    .bookAuthor(data.getBookAuthor())
                    .build())
    );

    int bookTotal = bookResponses.size();
    log.info("There are {} book found", bookTotal);

    return DataResponse.builder()
            .data(bookResponses)
            .build();
  }

}
