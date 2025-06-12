package kr.hhplus.be.server.order.infra.gateway.jpa;

import kr.hhplus.be.server.order.infra.gateway.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
}
