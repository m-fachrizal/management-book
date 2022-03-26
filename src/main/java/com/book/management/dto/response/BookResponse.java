package com.book.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class BookResponse {
  private Integer bookId;
  private Long isbn;
  private String bookTitle;
  private String bookAuthor;
}
