package com.book.management.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindAllBooksFromAuthorRequest {

    @NotBlank
    private String bookAuthor;
}
