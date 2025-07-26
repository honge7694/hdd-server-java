package kr.hhplus.be.server.orderservice.application.usecase.port.result;

import kr.hhplus.be.server.orderservice.application.usecase.port.result.dto.ProductItem;
import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PlaceOrderResult(
        Long id,
        Long userId,
        OrderStatus status,
        List<ProductItem> productList,
        int totalPrice,
        LocalDateTime createdAt
) {
}
