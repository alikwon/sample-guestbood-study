package org.alikwon.guestbook.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, ENTITY> {

    private List<DTO> dtoList;

    // 총 페이지 수
    private int totalPage;

    // 현재 페이지
    private int page;
    // 한페이지 출력 개수
    private int size;

    // 시작페이지 번호, 끝 페이지 번호
    private int start, end;

    // 이전, 다음
    private boolean prev, next;

    private List<Integer> pageList;

    public PageResultDTO(Page<ENTITY> result, Function<ENTITY, DTO> fn){
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable) {
        // 0부터 시작하므로 1추가
        this.page = pageable.getPageNumber() + 1;

        this.size = pageable.getPageSize();

        int tempEnd = (int)(Math.ceil(page/(double) size)) * size;
        start = tempEnd - size + 1;

        prev = start > 1;

        end = totalPage > tempEnd ? tempEnd : totalPage;

        next = totalPage > tempEnd;

//        for (int i = start; i <= end; i++) {
//            pageList.add(i);
//        }

        /*
            rangeClosed(start,end) : start 보다 크거나 같고 end 보다 작거나 같은 int 값을
            boxed() : auto-boxing 한 뒤에
            collect(Collectors.toList()) : List 로 결과를 가져온다
        */
        pageList = IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }
}
