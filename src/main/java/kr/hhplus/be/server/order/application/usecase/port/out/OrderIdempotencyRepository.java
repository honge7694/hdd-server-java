package kr.hhplus.be.server.order.application.usecase.port.out;

import kr.hhplus.be.server.order.domain.OrderIdempotency;

import java.util.Optional;

public interface OrderIdempotencyRepository {
    OrderIdempotency save(OrderIdempotency orderIdempotency);
    Optional<OrderIdempotency> findByIdempotencyKey(String idempotencyKey);

    void saveAndFlush(OrderIdempotency orderIdempotency);
}
