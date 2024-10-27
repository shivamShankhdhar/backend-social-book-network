package com.shivam.book.service.book;

import com.shivam.book.exception.book.OperationNotPermittedException;
import com.shivam.book.mapper.feedback.FeedBackMapper;
import com.shivam.book.model.book.Book;
import com.shivam.book.model.book.Feedback;
import com.shivam.book.model.user.User;
import com.shivam.book.repository.book.BookRepository;
import com.shivam.book.repository.book.FeedBackRepository;
import com.shivam.book.requests.book.FeedbackRequest;
import com.shivam.book.responses.book.FeedbackResponse;
import com.shivam.book.responses.book.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedBackMapper feedBackMapper;
    private final FeedBackRepository feedBackRepository;

    public Integer saveFeedback(FeedbackRequest request, Authentication connectedUser) {

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with the id:"+ " "+request.bookId()));

        if(book.isArchived() || !book.isSharable()){
            throw new OperationNotPermittedException("Feedback not available for this book");
        }

        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You can not give a feedback to you own book");
        }
        Feedback feedback = feedBackMapper.toFeedback(request);
        return feedBackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {

        Pageable pageable = PageRequest.of(page,size);
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedBackRepository.findAllByBookId(bookId,pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedBackMapper.toFeedbackResponse(f,user.getId()))
                .toList();
        return  new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
