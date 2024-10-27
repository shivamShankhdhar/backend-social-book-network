package com.shivam.book.Utilities;

import com.shivam.book.model.book.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(Integer ownerId){
        return (root,query,criteriaBuilder)-> criteriaBuilder.equal(root.get("owner").get("id"),ownerId);
    }
}
