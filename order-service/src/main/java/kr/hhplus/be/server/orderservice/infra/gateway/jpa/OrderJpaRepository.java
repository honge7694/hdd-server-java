package kr.hhplus.be.server.orderservice.infra.gateway.jpa;

import kr.hhplus.be.server.orderservice.infra.gateway.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
