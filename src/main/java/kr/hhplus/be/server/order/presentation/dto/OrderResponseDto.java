package kr.hhplus.be.server.order.presentation.dto;

import kr.hhplus.be.server.order.application.usecase.port.result.dto.ProductItem;
import kr.hhplus.be.server.order.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private OrderStatus orderStatus;
    private List<ProductItem> productItemList;
    private int totalPrice;
    private LocalDateTime orderDate;
}
