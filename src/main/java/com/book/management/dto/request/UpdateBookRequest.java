package com.book.management.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class UpdateBookRequest {

  @NotNull
  private Integer bookId;

  @NotNull
  private Long isbn;

  @NotBlank
  private String bookTitle;

  @NotBlank
  private String bookAuthor;

}
