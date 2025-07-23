package gift.item.dto;

import jakarta.validation.constraints.*;

public record OptionRequestDto(

        @NotBlank(message = "옵션 이름은 필수입니다")
        @Size(max = 50, message = "옵션 이름은 최대 50자까지 가능합니다")
        @Pattern(regexp = "^[a-zA-Z0-9 ()\\[\\]+\\-&/_]*$", message = "옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다.")
        String name,

        @NotNull(message = "옵션 수량은 필수입니다")
        @Min(value = 1, message = "옵션 수량은 최소 1개 이상입니다")
        @Max(value = 99_999_999, message = "옵션 수량은 최대 1억개 미만입니다")
        Integer quantity
) {
}
