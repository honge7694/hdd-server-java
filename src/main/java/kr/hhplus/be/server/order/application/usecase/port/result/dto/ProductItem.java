package kr.hhplus.be.server.order.application.usecase.port.result.dto;

public record ProductItem (
        Long id,
        String name,
        int price,
        int quantity
)

{}
