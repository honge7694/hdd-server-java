package kr.hhplus.be.server.order.infra.gateway.jpa;

import kr.hhplus.be.server.order.infra.gateway.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
