package kr.hhplus.be.server.orderservice.infra.gateway.custom;

import kr.hhplus.be.server.orderservice.domain.OutboxEvent;

import java.util.List;

public interface OutboxRepositoryCustom {
    OutboxEvent findByOrderId(Long orderId);
    List<OutboxEvent> findTop100ByOrderByCreatedAtAsc();
    void delete(OutboxEvent event);
}
