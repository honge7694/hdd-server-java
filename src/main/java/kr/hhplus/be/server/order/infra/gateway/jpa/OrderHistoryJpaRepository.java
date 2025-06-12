package kr.hhplus.be.server.order.infra.gateway.jpa;

import kr.hhplus.be.server.order.infra.gateway.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryJpaRepository extends JpaRepository<OrderHistoryEntity, Long> {
}
