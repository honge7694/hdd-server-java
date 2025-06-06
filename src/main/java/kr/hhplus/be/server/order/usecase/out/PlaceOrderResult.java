package kr.hhplus.be.server.order.usecase.out;

import kr.hhplus.be.server.order.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public record PlaceOrderResult(
        Long id,
        //Long userId,
        OrderStatus status,
        LocalDateTime createdAt
) {
}
