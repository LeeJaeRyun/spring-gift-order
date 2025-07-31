package gift.order.service;

import gift.order.client.KaKaoMessageClient;
import gift.order.dto.KaKaoMessageTemplate;
import gift.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KaKaoMessageService {

    private final KaKaoMessageClient kakaoMessageClient;
    private final KaKaoMessageTemplate kakaoMessageTemplate;

    public void sendOrderConfirmation(String accessToken, Order order) {
        String templateJson =kakaoMessageTemplate.createKakaoOrderMessage(order);
        kakaoMessageClient.sendMessage(accessToken, templateJson);
    }
}

