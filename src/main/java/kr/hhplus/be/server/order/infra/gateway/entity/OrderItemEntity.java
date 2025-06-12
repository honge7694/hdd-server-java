package kr.hhplus.be.server.order.infra.gateway.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.order.domain.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
public class OrderItemEntity {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private long id;

    private Long orderId;
    private Long productId;
    private int price;
    private int quantity;

    protected OrderItemEntity() {}

    public OrderItemEntity(Long orderId, Long productId, int price, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderItemEntity fromDomain(OrderItem orderItem) {
        return new OrderItemEntity(
                orderItem.getOrderId(),
                orderItem.getProductId(),
                orderItem.getPrice(),
                orderItem.getQuantity()
        );
    }

    public OrderItem toDomain() {
        return OrderItem.create(orderId, productId, price, quantity).assignId(id);
    }
}
