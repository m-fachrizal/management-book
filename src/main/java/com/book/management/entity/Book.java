package com.book.management.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "book")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "Book.getBooksFromAuthor",
        query = "select b from Book b where b.bookAuthor = ?1")
public class Book {

  @CreationTimestamp
  private LocalDateTime createdDate;

  @UpdateTimestamp
  private LocalDateTime updatedDate;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Integer bookId;

  @Column
  private Long isbn;

  @Column
  private String bookTitle;

  @Column
  private String bookAuthor;

}
