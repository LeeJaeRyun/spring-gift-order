package gift.order.service;

import gift.item.entity.Option;
import gift.item.repository.OptionRepository;
import gift.member.entity.Member;
import gift.order.dto.OrderRequest;
import gift.order.dto.OrderResponse;
import gift.order.entity.Order;
import gift.order.repository.OrderRepository;
import gift.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final WishlistRepository wishlistRepository;
    private final KaKaoMessageService kakaoMessageService;

    @Transactional
    public OrderResponse createOrder(
            Member member,
            OrderRequest request,
            String kakaoAccessToken
    ) {
        Option option = optionRepository.findById(request.optionId())
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));

        // 재고 부족 체크
        if (request.quantity() > option.getQuantity()) {
            throw new IllegalArgumentException("주문 수량이 재고 수량보다 많습니다.");
        }
        // 재고 차감
        option.decreaseQuantity(request.quantity());

        // 위시리스트에 있으면 삭제
        wishlistRepository.deleteByMemberAndItem(member, option.getItem());

        LocalDateTime now = LocalDateTime.now();

        // 주문 생성 및 저장
        Order order = Order.of(member, option, request, now);
        Order savedOrder = orderRepository.save(order);

        // 카카오 메시지 전송
        try {
            if (kakaoAccessToken != null && !kakaoAccessToken.isEmpty()) {
                kakaoMessageService.sendOrderConfirmation(kakaoAccessToken, savedOrder);
            }
        } catch (Exception e) {
            log.warn("카카오 메시지 전송 실패: {}", e.getMessage());
        }

        // 응답 DTO 생성 후 반환
        return new OrderResponse(
                savedOrder.getId(),
                option.getId(),
                savedOrder.getQuantity(),
                savedOrder.getMessage(),
                savedOrder.getOrderDateTime()
        );
    }

}
