package org.alikwon.guestbook.service.guestbook;

import org.alikwon.guestbook.dto.guestbook.GuestbookDTO;
import org.alikwon.guestbook.dto.PageRequestDTO;
import org.alikwon.guestbook.dto.PageResultDTO;
import org.alikwon.guestbook.entity.guestbook.GuestBook;

public interface GuestbookService {
    Long register(GuestbookDTO dto);

    PageResultDTO<GuestbookDTO, GuestBook> getList(PageRequestDTO requestDTO);

    GuestbookDTO read(Long gno);

    void remove(Long gno);
    void modify(GuestbookDTO dto);

    default GuestBook dotToEntity(GuestbookDTO dto){
        GuestBook entity = GuestBook.builder()
                .gno(dto.getGno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
        return entity;
    }

    default GuestbookDTO entityToDto(GuestBook entity){
        GuestbookDTO dto = GuestbookDTO.builder()
                .gno(entity.getGno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();

        return dto;
    }
}
