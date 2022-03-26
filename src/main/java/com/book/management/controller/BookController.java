package com.book.management.controller;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.request.UpdateBookRequest;
import com.book.management.dto.response.DataResponse;
import com.book.management.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@Tag(name = "BOOK API", description = "CRUD book data")
@RequestMapping(value = "/api/book")
public class BookController {

  @Autowired
  private BookService bookService;

  @PostMapping("/add")
  @Operation(summary = "Add one book", operationId = "Add One Book")
  public DataResponse<?> addBook(@Valid @RequestBody AddBookRequest addBookRequest) {
    log.info("Invoking get on /api/book/add route");
    return bookService.addBook(addBookRequest);
  }

  @GetMapping("/list")
  @Operation(summary = "Get all books", operationId = "Get All Books")
  public DataResponse<?> getAllBooks() {
    log.info("Invoking get on /api/book/list route");
    return bookService.getAllBooks();
  }

  @GetMapping("/id/{bookId}")
  @Operation(summary = "Get book from bookId", operationId = "Get Book")
  public DataResponse<Object> getBook(@PathVariable(value="bookId") Integer bookId) {
    log.info("Invoking get on /api/book/{bookId} route");
    return bookService.getBook(bookId);
  }

  @PutMapping("/update")
  @Operation(summary = "Update book from bookId", operationId = "Update Book")
  public DataResponse<?> updateBook(@Valid @RequestBody UpdateBookRequest bookRequest) {
    log.info("Invoking put on api/book/update route");
    return bookService.updateBook(bookRequest);
  }

  @DeleteMapping("/delete/{bookId}")
  @Operation(summary = "Delete book from bookId", operationId = "Delete Book")
  public DataResponse<?> deleteBook(@PathVariable(value="bookId") Integer bookId) {
    log.info("Invoking delete on /api/book/delete/{bookId} route");
    return bookService.deleteBook(bookId);
  }
}
