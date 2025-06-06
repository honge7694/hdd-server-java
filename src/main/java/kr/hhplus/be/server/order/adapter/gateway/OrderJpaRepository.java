package kr.hhplus.be.server.order.adapter.gateway;

import kr.hhplus.be.server.order.adapter.gateway.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long>, OrderRepositoryCustom {
}
