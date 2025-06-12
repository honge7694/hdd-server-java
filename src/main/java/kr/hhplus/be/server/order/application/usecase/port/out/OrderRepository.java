package kr.hhplus.be.server.order.application.usecase.port.out;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderHistory;
import kr.hhplus.be.server.order.domain.OrderItem;

public interface OrderRepository {
    Order save(Order order);
    OrderItem save(OrderItem orderItem);
    OrderHistory save(OrderHistory orderHistory);
}
