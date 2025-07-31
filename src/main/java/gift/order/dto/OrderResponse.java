package gift.order.dto;

import gift.order.entity.Order;

import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long optionId,
        int quantity,
        String message,
        LocalDateTime orderDateTime
) {
    public static OrderResponse of(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOption().getId(),
                order.getQuantity(),
                order.getMessage(),
                order.getOrderDateTime()
        );
    }
}
