package kr.hhplus.be.server.orderservice.infra.kafka.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderEvent {
    private Long orderId;
    private String correlationId;
}
