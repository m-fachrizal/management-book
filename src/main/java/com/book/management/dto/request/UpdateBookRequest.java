package com.book.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;


@Data
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
