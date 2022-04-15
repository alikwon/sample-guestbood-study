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