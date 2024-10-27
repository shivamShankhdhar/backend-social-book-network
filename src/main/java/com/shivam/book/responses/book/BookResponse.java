package com.shivam.book.responses.book;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    private int id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private boolean archived;
    private boolean sharable;
    private String owner;
    private byte[] cover;
    private double rate;
}
