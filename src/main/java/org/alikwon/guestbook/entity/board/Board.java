package org.alikwon.guestbook.entity.board;

import lombok.*;
import org.alikwon.guestbook.entity.BaseEntity;
import org.alikwon.guestbook.entity.member.Member;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "writer")
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;

    private String content;

    @ManyToOne
    private Member writer; // 연관 관계 정의
}
