package gift.item.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ItemNameValidator implements ConstraintValidator<ValidItemName, String> {

    // 허용 특수문자: ( ) [ ] + - & / _ 만 포함
    private static final Pattern VALID_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9 ()\\[\\]\\+\\-\\&/_]*$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return VALID_PATTERN.matcher(value).matches();
    }
}
