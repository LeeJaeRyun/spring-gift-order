package gift.item.dto;

import gift.item.entity.Option;

public record OptionResponseDto(
        Long id,
        String name,
        Integer quantity
) {
    public static OptionResponseDto fromEntity(Option option) {
        return new OptionResponseDto(
                option.getId(),
                option.getName(),
                option.getQuantity()
        );
    }
}
