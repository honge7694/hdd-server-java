package kr.hhplus.be.server.orderservice.application.usecase.port.out;

import kr.hhplus.be.server.orderservice.domain.Order;
import kr.hhplus.be.server.orderservice.domain.OrderHistory;
import kr.hhplus.be.server.orderservice.domain.OrderItem;

public interface OrderRepository {
    Order save(Order order);
    OrderItem save(OrderItem orderItem);
    OrderHistory save(OrderHistory orderHistory);
    int findAllSize();
}
