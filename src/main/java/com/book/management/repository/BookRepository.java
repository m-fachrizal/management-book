package com.book.management.repository;

import com.book.management.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
  public Optional<Book> findFirstByOrderByBookId();


}
