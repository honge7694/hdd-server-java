package kr.hhplus.be.server.order.infra.gateway.jpa;

import kr.hhplus.be.server.order.domain.OrderIdempotency;
import kr.hhplus.be.server.order.infra.gateway.entity.OrderIdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderIdempotencyJpaRepository extends JpaRepository<OrderIdempotencyEntity, Long> {

    Optional<OrderIdempotency> findByIdempotencyKey(String idempotencyKey);
}
