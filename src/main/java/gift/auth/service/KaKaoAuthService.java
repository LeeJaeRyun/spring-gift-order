package gift.auth.service;

import gift.auth.client.KaKaoAuthClient;
import gift.auth.dto.KaKaoLoginResponse;
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

    public KaKaoLoginResponse loginWithCode(String code) {
        KaKaoTokenResponse kakaoTokenResponse = kakaoAuthClient.requestAccessToken(code);
        String kakaoAccessToken = kakaoTokenResponse.accessToken();

        String email = kakaoAuthClient.requestUserEmail(kakaoAccessToken);

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(new Member(email, UUID.randomUUID().toString())));

        String jwtToken = jwtProvider.createToken(member, kakaoTokenResponse.expiresIn());

        return new KaKaoLoginResponse(jwtToken, kakaoAccessToken);
    }
}
