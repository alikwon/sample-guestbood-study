package org.alikwon.guestbook.repository.member;

import org.alikwon.guestbook.entity.board.Board;
import org.alikwon.guestbook.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MemberRepository extends JpaRepository<Member, String>, QuerydslPredicateExecutor {
}
