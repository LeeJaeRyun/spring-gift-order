package gift.order.entity;

import gift.item.entity.Option;
import gift.member.entity.Member;
import gift.order.dto.OrderRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id")
    private Option option;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String message;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    public Order(Member member, Option option, Integer quantity, String message, LocalDateTime orderDateTime) {
        this.member = member;
        this.option = option;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = orderDateTime;
    }

    public static Order of(Member member, Option option, OrderRequest request, LocalDateTime now) {
        return new Order(
                member,
                option,
                request.quantity(),
                request.message(),
                now
        );
    }

}
