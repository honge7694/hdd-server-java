package kr.hhplus.be.server.order.infra.gateway;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OutboxEvent;
import kr.hhplus.be.server.order.infra.gateway.entity.OrderEntity;
import kr.hhplus.be.server.order.infra.gateway.entity.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final EntityManager em;

    @Override
    public Order save(Order order, OutboxEvent event) {
        // 주문 저장
        OrderEntity orderEntity = OrderEntity.fromDomain(order);
        em.persist(orderEntity);
        // 메시징 처리
        OutboxEventEntity outboxEventEntity = OutboxEventEntity.fromDomain(event, orderEntity.getId());
        em.persist(outboxEventEntity);

        em.flush();

        return orderEntity.toDomain();
    }
}
