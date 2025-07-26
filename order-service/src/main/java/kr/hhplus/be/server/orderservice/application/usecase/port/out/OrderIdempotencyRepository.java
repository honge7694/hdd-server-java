package kr.hhplus.be.server.orderservice.application.usecase.port.out;

import kr.hhplus.be.server.orderservice.domain.OrderIdempotency;

import java.util.Optional;

public interface OrderIdempotencyRepository {
    OrderIdempotency save(OrderIdempotency orderIdempotency);
    Optional<OrderIdempotency> findByIdempotencyKey(String idempotencyKey);

    void saveAndFlush(OrderIdempotency orderIdempotency);
}
