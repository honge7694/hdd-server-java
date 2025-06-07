package kr.hhplus.be.server.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private long id;
    private String name;
    private String brand;
    private int price;
    private int stockQuantity;
}
