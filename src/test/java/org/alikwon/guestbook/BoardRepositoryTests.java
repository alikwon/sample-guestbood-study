package org.alikwon.guestbook;

import org.alikwon.guestbook.entity.board.Board;
import org.alikwon.guestbook.entity.member.Member;
import org.alikwon.guestbook.repository.board.BoardRepository;
import org.alikwon.guestbook.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void insertBoard(){
        for (long i = 1; i < 51; i++) {
            Member member = Member.builder()
                    .mno(i)
                    .build();
            Board board = Board.builder()
                    .title("Title - " + i)
                    .content("Content - " + i)
                    .writer(member)
                    .build();

            boardRepository.save(board);
        }
    }
}
