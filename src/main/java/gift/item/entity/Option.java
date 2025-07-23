package gift.item.entity;

import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "options", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"item_id", "name"})
})
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    protected Option() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Option(String name, Integer quantity, Item item) {
        this.name = name;
        this.quantity = quantity;
        this.item = item;
    }

    public void decreaseQuantity(int num) {

        if (num < 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY_DECREASE);
        }

        if (quantity < num) {
            quantity = 0;
        } else {
            quantity -= num;
        }
    }

}
