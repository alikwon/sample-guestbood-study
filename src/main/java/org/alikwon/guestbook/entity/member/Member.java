package org.alikwon.guestbook.entity.member;

import lombok.*;
import org.alikwon.guestbook.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mno;

    private String email;

    private String name;

    private String password;
}
