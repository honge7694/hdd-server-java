package kr.hhplus.be.server.order.adapter.gateway;

import kr.hhplus.be.server.order.adapter.gateway.entity.OrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, Long> {
}
