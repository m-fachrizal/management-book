package com.book.management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponse {
  private Integer bookId;
  private Long isbn;
  private String bookTitle;
  private String bookAuthor;
}
