package org.alikwon.guestbook.repository.board;

import org.alikwon.guestbook.entity.board.Board;
import org.alikwon.guestbook.entity.guestbook.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardRepository extends JpaRepository<Board, Long>, QuerydslPredicateExecutor {
}
