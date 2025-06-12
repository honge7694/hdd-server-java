package kr.hhplus.be.server.order.infra.gateway.custom;

import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OutboxEvent;

public interface OrderRepositoryCustom {

    Order save(Order order, OutboxEvent event);
}
