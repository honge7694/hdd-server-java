package kr.hhplus.be.server.orderservice.application.usecase.port.out;

import kr.hhplus.be.server.orderservice.domain.OutboxEvent;

public interface OrderMessageRepository {
    void saveOutboxEvent(OutboxEvent event);
    OutboxEvent findByOrderId(Long orderId);
}
