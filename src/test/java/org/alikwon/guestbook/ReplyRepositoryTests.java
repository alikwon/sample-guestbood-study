package org.alikwon.guestbook;

import org.alikwon.guestbook.entity.board.Board;
import org.alikwon.guestbook.entity.member.Member;
import org.alikwon.guestbook.entity.reply.Reply;
import org.alikwon.guestbook.repository.board.BoardRepository;
import org.alikwon.guestbook.repository.reply.ReplyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void insertReply(){
        for (long i = 1; i < 301; i++) {
            long bno = (long)(Math.random() * 50) + 1;
            Board board = Board.builder()
                    .bno(bno)
                    .build();
            Reply reply = Reply.builder()
                    .text("Reply - " + i)
                    .board(board)
                    .replyer("guest")
                    .build();
            replyRepository.save(reply);
        }
    }
}
