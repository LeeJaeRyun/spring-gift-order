package gift.global.resolver;

import com.fasterxml.classmate.MemberResolver;
import gift.global.annotation.LoginMember;
import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.member.auth.JwtProvider;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

//컨트롤러 메서드에 진입하기 전처리를 통해 객체를 주입할 수 있다.
//역할 : Spring MVC에서 컨트롤러 진입 전에 JWT 토큰을 파싱해서 @LoginMember가 붙은 파라미터에 Member 객체를 자동으로 주입해줌
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public LoginMemberArgumentResolver(JwtProvider jwtProvider, MemberRepository memberRepository) {
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    //처리할 파라미터인지 판단하는 메서드
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) &&
                parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String memberIdStr = (String) webRequest.getAttribute("memberId", NativeWebRequest.SCOPE_REQUEST);
        if (memberIdStr == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        Long memberId = Long.valueOf(memberIdStr);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
