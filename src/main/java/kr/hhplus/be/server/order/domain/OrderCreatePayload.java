package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.order.application.usecase.command.ProductItemCommand;

import java.util.List;

public class OrderCreatePayload {
    private Long userId;
    private Long couponId;
    private List<ProductItemCommand> items;

    private OrderCreatePayload(Long userId, Long couponId, List<ProductItemCommand> items) {
        this.userId = userId;
        this.couponId = couponId;
        this.items = items;
    }

    public static OrderCreatePayload create(Long userId, Long couponId, List<ProductItemCommand> items) {
        return new OrderCreatePayload(userId, couponId, items);
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public List<ProductItemCommand> getItems() {
        return items;
    }
}
