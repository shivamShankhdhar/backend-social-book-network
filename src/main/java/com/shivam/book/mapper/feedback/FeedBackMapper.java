package com.shivam.book.mapper.feedback;

import com.shivam.book.model.book.Book;
import com.shivam.book.model.book.Feedback;
import com.shivam.book.requests.book.FeedbackRequest;
import com.shivam.book.responses.book.FeedbackResponse;

import java.util.Objects;

public class FeedBackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .archived(false) // Not required and has no impact :: just to satisfy lombok
                        .sharable(false).build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(),id))
                .build();
    }
}
