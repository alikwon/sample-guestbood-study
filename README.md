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
```java
@SpringBootApplication
@EnableJpaAuditing
public class GuestbookStudyApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuestbookStudyApplication.class, args);
    }
}
```

- `@EnableJpaAuditing` : `AuditingEntityListener`를 활성화 하기 위해서 추가

---

# 엔티티클래스와 Querydsl 설정

## 동적쿼리 처리를 위한 Querydsl 설정

- `@Qeury` 를 통해서 많은 기능을 구현할 수 있지만, 선언시 **고정된 형태**를 가진다는 단점.
- 복잡한 검색조건이 필요한 상황에서는 동적쿼리 생성기능이 필요함

### Querydsl 라이브러리

- **Querydsl** 을 이용하면 복잡한 검색조건, 조인, 서브쿼리 등의 기능 구현이 가능함.
- 이용하기 위해서는 작성된 엔티티클래스가 아닌 **‘Q도메인’**  을 이용해야 함
- 라이브러리를 이용하기 위한  🐘 **build.gradle** 설정
    - plugins 항목에 querydsl 관련 부분 추가
    - dependencies 항목에 필요한 라이브러리를 추가
    - Gradle 에서 사용할 추가적이 task 추가

  💡**Spring 2.6.6 기준 build.gradle**

    ```java
    buildscript {
        ext {
            queryDslVersion = "5.0.0"
        }
    }
    
    plugins {
        id 'org.springframework.boot' version '2.6.6'
    
    ...
    
    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
    
        ...
    
        // QueryDSL
      implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"
      annotationProcessor(
              "javax.persistence:javax.persistence-api",
              "javax.annotation:javax.annotation-api",
              "com.querydsl:querydsl-apt:${queryDslVersion}:jpa")
    
      implementation group: 'org.modelmapper', name: 'modelmapper', version: '2.4.4'
      // https://mvnrepository.com/artifact/net.coobird/thumbnailator
      implementation group: 'net.coobird', name: 'thumbnailator', version: '0.4.14'
    }
    
    ...
    
    // Querydsl
    sourceSets {
        main {
            java {
                srcDirs = ["$projectDir/src/main/java", "$projectDir/build/generated"]
            }
        }
    }
    ```

    - 위 코드 추가후 `gradle clean` 후 `gradle compileJava` 실행
    - 실행이 완료되면 build 폴더안에 다음과 같은 구조가 생성됨

      ![생성된구조](https://i.imgur.com/9YLxkNa.png)
    - 그 후 `Repository` 인터페이스에 `QuerydslPredicateExecutor` 인터페이스를 추가상속함

      ```java
      import org.alikwon.guestbook.entity.GuestBook;
      import org.springframework.data.jpa.repository.JpaRepository;
      import org.springframework.data.querydsl.QuerydslPredicateExecutor;
    
      public interface GuestBookRepository 
                      extends JpaRepository<GuestBook, Long>, QuerydslPredicateExecutor {
      }
      ```

    - 테스트

        ```java
        //단일 항목
        @Autowired
        private GuestBookRepository guestBookRepository;
    
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
    
        // 다중 항목
        @Test
        public void testQueryMultiple(){
          Pageable pageable = PageRequest.of(0, 10, Sort.by("gno").descending());
    
          QGuestBook qGuestBook = QGuestBook.guestBook;
    
          String keyword = "11";
    
          BooleanBuilder builder = new BooleanBuilder();
    
          BooleanExpression expTitle = qGuestBook.title.contains(keyword);
          BooleanExpression expContent = qGuestBook.content.contains(keyword);
          BooleanExpression expWriter = qGuestBook.writer.contains(keyword);
    
            // title, content, writer 검색조건 or로 결합
          BooleanExpression expAll = expTitle.or(expContent).or(expWriter);
    	
          builder.and(expAll);
    
            // 113 < gno 검색조건 결합
          builder.and(qGuestBook.gno.gt(113L));
    
          Page<GuestBook> result = guestBookRepository.findAll(builder, pageable);
    
          result.forEach(guestBook -> {
              System.out.println(guestBook);
          });
        }
        ```

        1. 가장 먼저 동적처리를 위해 **Q도메인**클래스를 얻어 온다. ( `QGuestBook` )
        2. `BooleanBuilder` : where문에 들어가는 조건들을 넣어주는 컨테이너 느낌
        3. `BooleanBuilder` 에 들어가는 값은 `com.querydsl.core.types.Predicate` 타입이어야함

           💡Java 에 있는 Predicate 타입이 아니므로 주의

        4. 만들어진 조건은 where문에 and나 or 같은 키워드와 결합
        5. `BooleanBuilder`는 `QuerydslPredicateExecutor` 인터페이스의 `findAll()` 사용 가능
---

## 서비스 계층과 DTO

- **DTO** 는 엔티티 객체와 달리 **각 계층끼리 주고받는 우편물** 개념
- 실제 프로젝트를 작성할 경우 **DTO(Data Transfer Object)** 를 사용하는 방식을 권장한다.
    - JPA를 이용하게 되면 엔티티 객체는 단순히 데이터를 담는 객체가 아니라 내부적으로 엔티티 매니져(entity manager) 가 관리하는 객체이기 때문
    - 따라서 DTO가 일회성으로 데이터를 주고받는 용도로 사용되는 것과 달리 생명주가 자체가 전혀 다르므로 분리해서 처리하는 것을 권장함
    - DTO를 사용하면 엔티티 객체의 범위를 한정지을 수 있기 때문에 좀 더 안전한 코드 작성이 가능

    ```
    💡 DTO 사용 시 단점 
    	- Entity와 유사한 코드를 중복으로 개발한다는 점과
    	- DTO와 Entity 간의 변환 과정이 필요하다는 것
    ```

- DTO 클래스 정의

    ```java
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    
    import java.time.LocalDateTime;
    
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class GuestbookDTO {
        private Long gno;
        private String title;
        private String content;
        private String writer;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
    }
    ```

- Service 클래스 작성

    ```java
    public interface GuestbookService {
        Long register(GuestbookDTO dto);
    }
    ```

    ```java
    @Service
    @Log4j2
    public class GuestbookServiceImpl implements GuestbookService {
        @Override
        public Long register(GuestbookDTO dto) {
            return null;
        }
    }
    ```


## DTO를 엔티티로 변환, 등록

- DTO를 엔티티객체로 변환하는 작업이 필요
    - ModelMapper라이브러리나 MapStruct등을 이용하기도 한다.

    ```java
    public interface GuestbookService {
      Long register(GuestbookDTO dto);
    
      default GuestBook dotToEntity(GuestbookDTO dto){
        GuestBook entity = GuestBook.builder()
                .gno(dto.getGno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
        return entity;
      }
    }
    ```

  💡 JAVA 8 버전부터 인터페이스의 실제 내용을 가지는 코드를 `default` 키워드로 생성 가능

    ```java
    @Service
    @Log4j2
    @RequiredArgsConstructor //의존성 자동주입
    public class GuestbookServiceImpl implements GuestbookService {
    
        private final GuestBookRepository repository; // 반드시 final
    
        @Override
        public Long register(GuestbookDTO dto) {
            log.info("-------------dto--------------");
            log.info(dto);
            GuestBook entity = dotToEntity(dto);
    
            log.info(entity);
    
            repository.save(entity);
    
            return entity.getGno();
        }
    }
    ```

    - Impl클래스는 `GuestbookRepository` 를 주입하고 클래스 선언시 `@RequiredArgsConstructor`를 이용해서 자동으로 주입한다.
    - `@RequiredArgsConstructor`
        - `final`이 붙거나 `@NotNull` 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
        - `@RequiredArgsConstructor` 미사용시 생성자주입

        ```java
        private GuestBookRepository repository;
        
        @Autowired
        public GuustBookServiceImpl(GuestBookRepository repository){
        	this.repository = repository;
        }
        ```


---

# 목록 처리

- 고려사항
    - 화면에 필요한 목록 데이터에 대한 DTO생성
    - DTO를 Pageable 타입으로 전황
    - Page<Entity>를 화면에서 사용하기 쉬운 DTO의 리스트 등으로 변환
    - 화면에 필요한 페이지 번호처리

## 목록처리를 위한 DTO

- 목록처리 작업은 모든 게시판관련 기능에서 사용될 가능성이 높음.
- 따라서 재사용이 가능한 구조를 생성

### 페이지 요청 처리 DTO (PageRequestDTO)

- 요청할 때 사용하는 데이터를 재사용하기 쉽게 만드는 클래스
- ex ) 페이지 번호, 페이지내 목록 개수, 검색조건...

```java
@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
    private int page;
    private int size;

    public PageRequestDTO(){
				// 기본 값
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort){
				//JPA 의 경우 페이지 번호가 0부터 시작하므로 page - 1
        return PageRequest.of(page - 1, size, sort);
    }
}
```

### 페이지 결과 처리DTO (PageResultDTO)

- Page<Entity> 의 엔티티객체들을 DTO 객체로 변환해서 자료구조로 담아줘야함
- 화면 출력에 필요한 페이지 정보 구성

```java
@Data
public class PageResultDTO<DTO, ENTITY> {

    private List<DTO> dtoList;

    public PageResultDTO(Page<ENTITY> result, Function<ENTITY, DTO> fn){
        dtoList = result.stream().map(fn).collect(Collectors.toList());
    }
}
```

- 해당 클래스를 다양한 곳에서 사용할 수 있도록 제네릭 타입 이용 ( <DTO, ENTITY> )
- 생성자
    - `Page<ENTITY>` 타입을 이용해서 `List<DTO>` 타입의 list를 생성
    - `Function<ENTITY, DTO>` 는 ENTITY를 변환 로직을 거쳐서 DTO로 변환해주는 기능