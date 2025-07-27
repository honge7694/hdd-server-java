package kr.hhplus.be.server.orderservice.application.usecase.command;

public record ProductItemCommand(
        Long productId,
        int quantity
) {
}
