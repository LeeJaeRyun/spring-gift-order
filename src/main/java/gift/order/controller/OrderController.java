package gift.order.controller;

import gift.global.annotation.LoginMember;
import gift.global.exception.CustomException;
import gift.item.entity.Option;
import gift.item.repository.OptionRepository;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.order.dto.OrderRequest;
import gift.order.dto.OrderResponse;
import gift.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OptionRepository optionRepository;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @LoginMember Member member,
            @RequestBody OrderRequest orderRequest,
            @RequestHeader(value = "Kakao-Access-Token", required = false) String kakaoAccessToken
    ) {
        OrderResponse response = orderService.createOrder(member, orderRequest, kakaoAccessToken);
        return ResponseEntity.ok(response);
    }
}
