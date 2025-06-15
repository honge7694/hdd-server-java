package kr.hhplus.be.server.order.application.usecase.port.result;

import kr.hhplus.be.server.order.application.usecase.port.result.dto.ProductItem;
import kr.hhplus.be.server.order.domain.enums.OrderStatus;
import kr.hhplus.be.server.product.model.Product;

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
