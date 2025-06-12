package kr.hhplus.be.server.order.infra.gateway.custom;

import kr.hhplus.be.server.order.domain.OutboxEvent;

public interface OutboxRepositoryCustom {
    OutboxEvent findByOrderId(Long orderId);
}
