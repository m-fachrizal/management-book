package com.book.management.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class GetBooksFromAuthorRequest {
    @NotBlank
    private String bookAuthor;
}
