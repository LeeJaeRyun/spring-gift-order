package gift.auth.service;

import gift.auth.client.KaKaoAuthClient;
import gift.auth.dto.KaKaoTokenResponse;
import gift.member.auth.JwtProvider;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class KaKaoAuthService {

    private final KaKaoAuthClient kakaoAuthClient;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KaKaoAuthService(KaKaoAuthClient kakaoAuthClient, MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.kakaoAuthClient = kakaoAuthClient;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public String createKaKaoLoginUrl() {
        return kakaoAuthClient.buildLoginUrl();
    }

    public String loginWithCode(String code) {
        KaKaoTokenResponse kakaoTokenResponse = kakaoAuthClient.requestAccessToken(code);
        String accessToken = kakaoTokenResponse.accessToken();
        int kakaoExpiresIn = kakaoTokenResponse.expiresIn();

        String email = kakaoAuthClient.requestUserEmail(accessToken);

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(new Member(email, UUID.randomUUID().toString())));

        return jwtProvider.createToken(member, kakaoExpiresIn);
    }
}
