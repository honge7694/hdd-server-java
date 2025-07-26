package kr.hhplus.be.server.orderservice.infra.gateway.jpa;

import kr.hhplus.be.server.orderservice.infra.gateway.entity.OrderIdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderIdempotencyJpaRepository extends JpaRepository<OrderIdempotencyEntity, Long> {

    Optional<OrderIdempotencyEntity> findByIdempotencyKey(String idempotencyKey);
}
