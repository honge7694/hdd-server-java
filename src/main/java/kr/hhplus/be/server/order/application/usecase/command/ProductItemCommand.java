package kr.hhplus.be.server.order.application.usecase.command;

public record ProductItemCommand(
        Long productId,
        int quantity
) {
}
