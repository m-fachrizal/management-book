package com.book.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddBookRequest {

    @NotNull
    private Integer isbn;

    @NotBlank
    private String bookTitle;

    @NotBlank
    private String bookAuthor;
}
