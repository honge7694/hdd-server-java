package kr.hhplus.be.server.orderservice.infra.gateway.custom;

import kr.hhplus.be.server.orderservice.domain.Order;
import kr.hhplus.be.server.orderservice.domain.OutboxEvent;
import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;

public interface OrderRepositoryCustom {

    Order save(Order order, OutboxEvent event);
    void updateOrderStatus(Long orderId, OrderStatus status);
}
