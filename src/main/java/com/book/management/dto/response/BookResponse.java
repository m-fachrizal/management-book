package com.book.management.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponse {
  private Integer bookId;
  private Integer isbn;
  private String bookTitle;
  private String bookAuthor;
}
