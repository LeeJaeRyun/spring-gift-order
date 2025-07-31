package gift.order.dto;

public record OrderRequest(
        Long optionId,
        int quantity,
        String message
) {
}
