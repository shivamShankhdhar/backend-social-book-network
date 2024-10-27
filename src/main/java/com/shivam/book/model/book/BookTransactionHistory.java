package com.shivam.book.model.book;

import com.shivam.book.common.BaseEntity;
import com.shivam.book.model.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BookTransactionHistory extends BaseEntity {

    // user relationship
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // one transaction history will be associated with only one user
    // book relationship
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book; // one transaction history will be associated with only one book

    private boolean returned;
    private boolean returnApproved;



}
