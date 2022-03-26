package com.book.management.repository;

import com.book.management.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {

  List<Book> getBooksFromAuthor(String bookAuthor);

  @Query(value = "SELECT * FROM BOOK ORDER  BY ISBN DESC ", nativeQuery = true)
  List<Book> findAllBooksOrderByIsbn();

}
