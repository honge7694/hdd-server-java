package kr.hhplus.be.server.order.infra.gateway;

import kr.hhplus.be.server.order.infra.gateway.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {
}
