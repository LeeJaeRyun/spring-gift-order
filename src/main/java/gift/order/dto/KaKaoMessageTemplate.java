package gift.order.dto;

import gift.order.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class KaKaoMessageTemplate {

    public static final String OBJECT_TYPE = "text";
    public static final String BUTTON_TITLE = "주문 내역 보기";
    public static final String URL_FORMAT = "https://yourdomain.com/orders/%d";
    public static final String TEXT_TEMPLATE = "주문이 완료되었습니다!\\n상품: %s\\n수량: %d\\n메시지: %s";

    public String createKakaoOrderMessage(Order order) {
        String text = String.format(TEXT_TEMPLATE,
                order.getOption().getItem().getName(),
                order.getQuantity(),
                order.getMessage());

        String webUrl = String.format(URL_FORMAT, order.getId());

        return """
    {
        "object_type": "%s",
        "text": "%s",
        "link": {
            "web_url": "%s"
        },
        "button_title": "%s"
    }
    """.formatted(OBJECT_TYPE, text, webUrl, BUTTON_TITLE);
    }
}
