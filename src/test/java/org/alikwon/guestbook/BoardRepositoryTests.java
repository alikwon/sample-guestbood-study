package org.alikwon.guestbook;

import org.alikwon.guestbook.entity.board.Board;
import org.alikwon.guestbook.entity.member.Member;
import org.alikwon.guestbook.repository.board.BoardRepository;
import org.alikwon.guestbook.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    /**
     * @Transactional 을 사용
     *  - Lazy loading을 사용하여 join이 발생하지 않음 -> board.getWriter()에서 예외 발생
     *  - 해당 메서드를 하나의 트렌젝션으로 처리하므로써 board.getWriter() 시에 member 테이블을 로딩하게됨
     */
    @Transactional
    @Test
    public void testRead1(){
        Optional<Board> result = boardRepository.findById(50L);

        Board board = result.get();
        System.out.println(board);
        System.out.println(board.getWriter());
    }

    @Test
    public void testReadWithWriter(){
        Object result = boardRepository.getBoardWithWriter(50L);
        Object[] arr = (Object[]) result;
        System.out.println("=================================");
        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void testGetBoardWithReply(){
        List<Object[]> result = boardRepository.getBoardWithReply(50L);

        for (Object[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
    }


}
