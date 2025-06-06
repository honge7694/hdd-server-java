package kr.hhplus.be.server.order.adapter.gateway;

import kr.hhplus.be.server.order.adapter.gateway.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
}
