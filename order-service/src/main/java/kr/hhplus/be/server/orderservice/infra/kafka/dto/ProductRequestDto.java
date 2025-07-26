package kr.hhplus.be.server.orderservice.infra.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private Long productId;
    private String correlationId;
}