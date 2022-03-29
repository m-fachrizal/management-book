package com.book.management.service;

import com.book.management.dto.request.AddBookRequest;
import com.book.management.dto.request.FindAllBooksFromAuthorRequest;
import com.book.management.dto.request.UpdateBookRequest;
import com.book.management.dto.response.DataResponse;

public interface BookService {
  DataResponse<Object> addBook(AddBookRequest addBookRequest);
  DataResponse<Object> getAllBooks();
  DataResponse<Object> getBook(Integer bookId);
  DataResponse<Object> updateBook(UpdateBookRequest bookRequest);
  DataResponse<Object> deleteBook(Integer bookId);
  DataResponse<Object> findAllBooksFromAuthor(FindAllBooksFromAuthorRequest findAllBooksFromAuthorRequest);
  DataResponse<Object> findAllBooksOrderByIsbn();

}
