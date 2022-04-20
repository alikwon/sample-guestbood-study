package org.alikwon.guestbook;

import org.alikwon.guestbook.entity.member.Member;
import org.alikwon.guestbook.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void insertMember(){
        for (int i = 1; i < 51; i++) {
            Member member = Member.builder()
                    .email("user" + i + "@test.com")
                    .password("1234")
                    .name("유저" + i)
                    .build();
            memberRepository.save(member);
        }
    }
}
