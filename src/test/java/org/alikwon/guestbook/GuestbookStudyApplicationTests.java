package org.alikwon.guestbook;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.alikwon.guestbook.dto.GuestbookDTO;
import org.alikwon.guestbook.dto.PageRequestDTO;
import org.alikwon.guestbook.dto.PageResultDTO;
import org.alikwon.guestbook.entity.GuestBook;
import org.alikwon.guestbook.entity.QGuestBook;
import org.alikwon.guestbook.repository.GuestBookRepository;
import org.alikwon.guestbook.service.GuestbookService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@SpringBootTest
class GuestbookStudyApplicationTests {

    @Autowired
    private GuestBookRepository guestBookRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void insertDummies() {
        for (int i = 1; i < 301; i++) {
            GuestBook guestbook = GuestBook.builder()
                    .title("Title - " + i)
                    .content("Content - " + i)
                    .writer("user" + (i % 10))
                    .build();
            guestBookRepository.save(guestbook);
        }
    }

    @Test
    public void updateTest() {
        // 존재하는 번호로 테스트
        long gno = 300L;
        Optional<GuestBook> result = guestBookRepository.findById(gno);

        if (result.isPresent()) {
            GuestBook guestBook = result.get();

            guestBook.changeTitle("Change Title - " + gno);
            guestBook.changeContent("Change Content - " + gno);

            guestBookRepository.save(guestBook);
        }
    }

    @Test
    public void testQuerySingular() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestBook qGuestBook = QGuestBook.guestBook;

        String keyword = "1";

        BooleanBuilder builder = new BooleanBuilder();

        BooleanExpression expression = qGuestBook.title.contains(keyword);

        builder.and(expression);

        Page<GuestBook> result = guestBookRepository.findAll(builder, pageable);

        result.forEach(guestBook -> {
            System.out.println(guestBook);
        });
    }

    @Test
    public void testQueryMultiple() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());

        QGuestBook qGuestBook = QGuestBook.guestBook;

        String keyword = "11";

        BooleanBuilder builder = new BooleanBuilder();

        BooleanExpression expTitle = qGuestBook.title.contains(keyword);
        BooleanExpression expContent = qGuestBook.content.contains(keyword);
        BooleanExpression expWriter = qGuestBook.writer.contains(keyword);
        BooleanExpression expAll = expTitle.or(expContent).or(expWriter);

        builder.and(expAll);

        builder.and(qGuestBook.gno.gt(113L));

        Page<GuestBook> result = guestBookRepository.findAll(builder, pageable);

        result.forEach(guestBook -> {
            System.out.println(guestBook);
        });
    }


    @Autowired
    GuestbookService service;
    @Test
    public void testRegister() {
        GuestbookDTO guestbookDTO = GuestbookDTO.builder()
                .title("Sample Title")
                .content("Sample Content")
                .writer("Sample writer")
                .build();
        System.out.println(service.register(guestbookDTO));
    }

    @Test
    public void testList(){
        PageRequestDTO requestDTO = new PageRequestDTO();
        requestDTO.setPage(11);
        PageResultDTO<GuestbookDTO,GuestBook> resultDTO = service.getList(requestDTO);

        for (GuestbookDTO guestbookDTO : resultDTO.getDtoList()) {
            System.out.println(guestbookDTO);
        }

        System.out.println("PREV\t: " + resultDTO.isPrev());
        System.out.println("NEXT\t: " + resultDTO.isNext());
        System.out.println("TOTAL\t: " + resultDTO.getTotalPage());
        System.out.println("========================================");
        for (GuestbookDTO dto : resultDTO.getDtoList()) {
            System.out.println(dto);
        }
        System.out.println("========================================");
        for (Integer i : resultDTO.getPageList()) {
            System.out.println(i);
        }
    }
}
