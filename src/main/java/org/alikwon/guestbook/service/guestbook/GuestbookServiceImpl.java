package org.alikwon.guestbook.service.guestbook;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.alikwon.guestbook.dto.guestbook.GuestbookDTO;
import org.alikwon.guestbook.dto.PageRequestDTO;
import org.alikwon.guestbook.dto.PageResultDTO;
import org.alikwon.guestbook.entity.guestbook.GuestBook;
import org.alikwon.guestbook.entity.guestbook.QGuestBook;
import org.alikwon.guestbook.repository.guestbook.GuestBookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestBookRepository repository;


    @Override
    public Long register(GuestbookDTO dto) {
        log.info("-------------dto--------------");
        log.info(dto);
        GuestBook entity = dotToEntity(dto);

        log.info(entity);

        repository.save(entity);

        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, GuestBook> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

        BooleanBuilder booleanBuilder = getSearch(requestDTO); // 검색조건 처리

        Page<GuestBook> result = repository.findAll(booleanBuilder, pageable); // Querydsl 사용

        Function<GuestBook, GuestbookDTO> fn = (entity -> entityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public GuestbookDTO read(Long gno) {
        Optional<GuestBook> result = repository.findById(gno);
        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    @Override
    public void remove(Long gno) {
        repository.deleteById(gno);
    }

    @Override
    public void modify(GuestbookDTO dto) {
        Optional<GuestBook> result = repository.findById(dto.getGno());
        if (result.isPresent()) {
            GuestBook entity = result.get();
            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);
        }
    }

    private BooleanBuilder getSearch(PageRequestDTO requestDTO) {
        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QGuestBook qGuestBook = QGuestBook.guestBook;

        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qGuestBook.gno.gt(0L);

        booleanBuilder.and(expression);

        if(type == null || type.trim().length() == 0){
            return booleanBuilder;
        }

        // 검색조건
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if(type.contains("t")){
            conditionBuilder.or(qGuestBook.title.contains(keyword));
        }
        if(type.contains("c")){
            conditionBuilder.or(qGuestBook.content.contains(keyword));
        }
        if(type.contains("w")){
            conditionBuilder.or(qGuestBook.writer.contains(keyword));
        }

        // 모든 검색조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }
}
