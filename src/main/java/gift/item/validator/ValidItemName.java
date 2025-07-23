package gift.item.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented //javadoc에 문서화됨 (API 문서에 표시됨)
@Retention(RetentionPolicy.RUNTIME) //런타임에도 유지됨을 의미함
@Constraint(validatedBy = ItemNameValidator.class) // 실제 검증 로직 클래스 지정
@Target({ElementType.FIELD}) // 이 어노테이션은 필드에만 붙일 수 있다는 뜻
public @interface ValidItemName {

    String message() default "상품 이름 형식이 잘못되었습니다."; //유효성 검사 실패시 사용자에게 보여줄 메시지로 Spring Validation이 내부적으로 자동으로 처리해줌.

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
