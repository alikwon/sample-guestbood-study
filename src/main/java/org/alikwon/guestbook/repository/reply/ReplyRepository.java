package org.alikwon.guestbook.repository.reply;

import org.alikwon.guestbook.entity.board.Board;
import org.alikwon.guestbook.entity.reply.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReplyRepository extends JpaRepository<Reply, Long>, QuerydslPredicateExecutor {
}
