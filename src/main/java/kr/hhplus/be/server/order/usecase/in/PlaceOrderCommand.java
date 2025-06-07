package kr.hhplus.be.server.order.usecase.in;

import org.springframework.lang.Nullable;

import java.util.List;

public record PlaceOrderCommand(
        Long userId,
        @Nullable Long userCouponId,
        List<ProductItemCommand> items
) {
}
