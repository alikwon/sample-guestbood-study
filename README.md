# sample-guestbood-study

- 학습한 JPA 와  Thymeleaf 를 조합한다.
---
# 프로젝트 와이어 프레임

- 화면구성
    - 목록화면 :
        - 제목/내용/작성자 항목으로 검색
        - 페이징처리
    - 등록화면
        - 새로운글 등록
        - 등록처리 후 목록화면
    - 조회화면
        - 특정 글 조회시 이동하는 화면
        - 수정/삭제화면 이동 버튼
    - 수정/삭제화면
        - 수정화면에서 삭제가 가능하고 삭제 후 목록화면
        - 수정한 경우 수정글 조회화면
- 기능

    | 기능 | URL ( /guestbook/* )      | Method | 설명 | redirect (/guestbook/* ) |
    |-----------| --- | --- |--------------------------| --- |
    | 목록 | /list     | GET | 목록/페이징/검색 |                          |
    | 등록 | /register | GET | 입력화면 |                          |
    | 등록 | /register | POST | 등록처리 | /list                    |
    | 조회 | /read     | GET | 조회화면 |                          |
    | 수정 | /modify   | GET | 수정/삭제화면 |                          |
    | 수정 | /modify   | POST | 수정처리 | /read                    |
    | 삭제 | /remove   | POST | 삭제처리 | /list                    |
---
# spring boot 프로젝트 생성

- Java Version : 8
- Type : gradle
- packaging : war
- 의존성 설정
    - Spring Boot DevTools
    - Lombok
    - Spring Web
    - Thymeleaf
    - MariaDB

---
## 자동으로 처리되는 날짜 / 시간 설정

```java
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "REG_DT", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "MOD_DT")
    private LocalDateTime modDate;
}
```

- `@MappedSuperclass`
    - 이 어노테이션이 적용된 클래스는 **테이블이 생성되지 않음**
    - 실제 테이블은 어노테이션이 적용된 클래스를 **상속한 엔티티의 클래스**로 생성된다
- **JPA**에서 관리되는 엔티티 객체들은 **영속 콘텍스트(Persistence Context)** 에서 관리됨.
    - SQL처리 후에도 객체는 계속 유지되고 필요할 때 꺼내서 **재사용**하는 방식
- `AuditingEntityListener`
    - 엔티티객체에 **변화가 일어나는 것을 감지하는 리스너(Listener)**
- `@CreatedDate` : 엔티티 생성시간 처리
- `@LastModifiedDate` : 최종 수정시간 처리
- `updatable속성` : false 로 설정시 엔티티객체를 DB에 반영할때 regDate 컬럼값은 변경되지 않음