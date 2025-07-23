package gift.member.service;

import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.member.auth.JwtProvider;
import gift.member.dto.MemberLoginRequestDto;
import gift.member.dto.MemberRegisterRequestDto;
import gift.member.dto.TokenResponseDto;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public MemberService(MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder,
                         JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    /**
     * 회원가입
     */
    @Transactional
    public void register(MemberRegisterRequestDto dto) {
        if (memberRepository.findByEmail(dto.email()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATE);
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        Member member = new Member(dto.email(), encodedPassword);
        memberRepository.save(member);
    }

    /**
     * 로그인
     * @return JWT 토큰 응답 DTO
     */
    @Transactional(readOnly = true)
    public TokenResponseDto login(MemberLoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.email())
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(dto.password(), member.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }

        String token = jwtProvider.createToken(member);
        return new TokenResponseDto(token);
    }
}