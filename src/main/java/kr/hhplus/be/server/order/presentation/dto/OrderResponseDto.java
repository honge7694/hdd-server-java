package kr.hhplus.be.server.order.presentation.dto;

import kr.hhplus.be.server.order.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
}
