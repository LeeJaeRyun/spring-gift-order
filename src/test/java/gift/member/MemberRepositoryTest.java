package gift.member;

import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원 조회 성공")
    void findByEmailSuccess() {
        // given
        Member member = new Member("test@example.com", "password123");
        memberRepository.save(member);

        // when
        Optional<Member> result = memberRepository.findByEmail("test@example.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("이메일로 회원 조회 실패")
    void findByEmailFail() {
        // when
        Optional<Member> result = memberRepository.findByEmail("notfound@example.com");

        // then
        assertThat(result).isEmpty();
    }
}
