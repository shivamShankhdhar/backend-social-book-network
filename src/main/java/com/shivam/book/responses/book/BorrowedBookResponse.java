package com.shivam.book.responses.book;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {

    private int id;
    private String title;
    private String authorName;
    private String isbn;
    private boolean archived;
    private boolean sharable;
    private double rate;
    private boolean returned;
    private boolean returnApproved;

}
