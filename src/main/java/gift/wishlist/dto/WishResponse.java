package gift.wishlist.dto;

public record WishResponse(
        Long itemId,
        String itemName,
        int price,
        String imageUrl
) {}

