package kr.hhplus.be.server.order.application.usecase.command;

import org.springframework.lang.Nullable;

import java.util.List;

public record PlaceOrderCommand(
        Long userId,
        @Nullable Long userCouponId,
        List<ProductItemCommand> items
) {
}
