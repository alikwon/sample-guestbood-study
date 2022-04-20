package org.alikwon.guestbook.entity.reply;

import lombok.*;
import org.alikwon.guestbook.entity.BaseEntity;
import org.alikwon.guestbook.entity.board.Board;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "board")
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    private String text;

    private String replyer;

    @ManyToOne
    private Board board; // 연관관계 지정
}
