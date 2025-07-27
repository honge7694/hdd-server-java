package kr.hhplus.be.server.orderservice.application.usecase.port.result.dto;

public record ProductItem (
        Long id,
        String name,
        int price,
        int quantity
)

{}

