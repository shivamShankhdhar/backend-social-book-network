package com.shivam.book.requests.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String title,

        @NotNull(message = "101")
        @NotEmpty(message = "101")
        String authorName,

        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String isbn,

        @NotNull(message = "103")
        @NotEmpty(message = "103")
        String synopsis,

        @NotNull(message = "104")
        @NotEmpty(message = "104")
        String bookCover,

        @NotNull(message = "105")
        @NotEmpty(message = "105")
        boolean sharable

) {

}
