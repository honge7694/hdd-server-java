package kr.hhplus.be.server.orderservice.infra.gateway.custom;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.orderservice.domain.Order;
import kr.hhplus.be.server.orderservice.domain.OutboxEvent;
import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;
import kr.hhplus.be.server.orderservice.infra.gateway.entity.OrderEntity;
import kr.hhplus.be.server.orderservice.infra.gateway.entity.OutboxEventEntity;
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

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        OrderEntity orderEntity = em.find(OrderEntity.class, orderId);
        if (orderEntity != null) {
            orderEntity.setOrderStatus(status);
            em.merge(orderEntity);
        }
    }
}
