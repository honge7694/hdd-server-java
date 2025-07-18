package kr.hhplus.be.server.productservice.product.dto;

public record ProductEvent(
        Long productId,
        String productName,
        int price,
        int stock
) {
}
