package kr.hhplus.be.server.orderservice.infra.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {
    private Long id;
    private String name;
    private int price;
    private int stock;
    private String correlationId;
}