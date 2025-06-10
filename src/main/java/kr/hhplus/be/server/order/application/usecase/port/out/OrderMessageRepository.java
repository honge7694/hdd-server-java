package kr.hhplus.be.server.order.application.usecase.port.out;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OutboxEvent;

public interface OrderMessageRepository {
    Order save(Order order, OutboxEvent event);
}
