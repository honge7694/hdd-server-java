package kr.hhplus.be.server.order.usecase.in;

public record ProductItemCommand(
        Long productId,
        int quantity
) {
}
