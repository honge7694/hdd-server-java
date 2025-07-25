package kr.hhplus.be.server.productservice.product.dto;

import kr.hhplus.be.server.productservice.product.model.Product;

public record ProductResponseDto(
        long id,
        String name,
        String category,
        String brand,
        int price,
        int stockQuantity
) {
    public ProductResponseDto(Product product) {
        this(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getBrand(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }
}
