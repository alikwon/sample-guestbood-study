package org.alikwon.guestbook.repository.guestbook;

import org.alikwon.guestbook.entity.guestbook.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface GuestBookRepository extends JpaRepository<GuestBook, Long>, QuerydslPredicateExecutor {
}
