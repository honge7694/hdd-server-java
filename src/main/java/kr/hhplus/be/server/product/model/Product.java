package kr.hhplus.be.server.product.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.exception.ConflictException;
import lombok.Getter;

@Entity
@Getter
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long id;

    private String name;
    private String category;
    private String brand;
    private int price;
    private int stockQuantity;

    @Version
    private Long version;

    protected Product() {}

    public Product(String name, String category, String brand, int price, int stockQuantity) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void reduceStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if (restStock < 0) {
            throw new ConflictException("상품의 개수가 부족합니다.");
        }
        stockQuantity = restStock;
    }
}
