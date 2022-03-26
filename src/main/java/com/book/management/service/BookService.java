package com.book.management.service;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.response.BookResponse;
import com.book.management.dto.request.UpdateBookRequest;
import com.book.management.dto.response.DataResponse;

public interface BookService {
  DataResponse<?> addBook(AddBookRequest addBookRequest);
  DataResponse<?> getAllBooks();
  DataResponse<Object> getBook(Integer bookId);
  DataResponse<?> updateBook(UpdateBookRequest bookRequest);
  DataResponse<?> deleteBook(Integer bookId);

}
