package gift.order.service;

import gift.order.client.KaKaoMessageClient;
import gift.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KaKaoMessageService {

    private final KaKaoMessageClient kakaoMessageClient;

    public void sendOrderConfirmation(String accessToken, Order order) {
        String templateJson = generateTemplate(order);
        kakaoMessageClient.sendMessage(accessToken, templateJson);
    }

    private String generateTemplate(Order order) {
        return """
        {
            "object_type": "text",
            "text": "주문이 완료되었습니다!\\n상품: %s\\n수량: %d\\n메시지: %s",
            "link": {
                "web_url": "https://yourdomain.com/orders/%d"
            },
            "button_title": "주문 내역 보기"
        }
        """.formatted(
                order.getOption().getItem().getName(),
                order.getQuantity(),
                order.getMessage(),
                order.getId()
        );
    }
}

