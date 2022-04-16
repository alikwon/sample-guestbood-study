package org.alikwon.guestbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.alikwon.guestbook.dto.GuestbookDTO;
import org.alikwon.guestbook.dto.PageRequestDTO;
import org.alikwon.guestbook.dto.PageResultDTO;
import org.alikwon.guestbook.entity.GuestBook;
import org.alikwon.guestbook.repository.GuestBookRepository;
import org.alikwon.guestbook.service.GuestbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        Page<GuestBook> result = repository.findAll(pageable);

        return new PageResultDTO<>(result, this::entityToDto);
    }
}
