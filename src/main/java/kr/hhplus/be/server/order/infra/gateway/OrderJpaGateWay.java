package kr.hhplus.be.server.order.infra.gateway;

import kr.hhplus.be.server.order.application.usecase.port.out.OrderMessageRepository;
import kr.hhplus.be.server.order.domain.OutboxEvent;
import kr.hhplus.be.server.order.infra.gateway.entity.OrderEntity;
import kr.hhplus.be.server.order.infra.gateway.entity.OrderHistoryEntity;
import kr.hhplus.be.server.order.infra.gateway.entity.OrderItemEntity;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderHistory;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderJpaGateWay implements OrderRepository, OrderMessageRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderHistoryRepository orderHistoryRepository;
//    @Qualifier("orderRepositoryCustomImpl")
    private final OrderRepositoryCustom orderRepositoryCustom;

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = OrderEntity.fromDomain(order);
        OrderEntity savedOrder = orderJpaRepository.save(orderEntity);
        return savedOrder.toDomain();
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        OrderItemEntity orderItemEntity = OrderItemEntity.fromDomain(orderItem);
        OrderItemEntity savedOrderItem = orderItemJpaRepository.save(orderItemEntity);
        return savedOrderItem.toDomain();
    }

    @Override
    public OrderHistory save(OrderHistory orderHistory) {
        OrderHistoryEntity orderHistoryEntity = OrderHistoryEntity.fromDomain(orderHistory);
        OrderHistoryEntity savedOrderHistoryEntity = orderHistoryRepository.save(orderHistoryEntity);
        return savedOrderHistoryEntity.toDomain();
    }

    @Override
    public Order save(Order order, OutboxEvent event) {
        return orderRepositoryCustom.save(order, event);
    }
}
