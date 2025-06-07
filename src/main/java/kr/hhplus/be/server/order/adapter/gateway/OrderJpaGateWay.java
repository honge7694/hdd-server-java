package kr.hhplus.be.server.order.adapter.gateway;

import kr.hhplus.be.server.order.adapter.gateway.entity.OrderEntity;
import kr.hhplus.be.server.order.adapter.gateway.entity.OrderHistoryEntity;
import kr.hhplus.be.server.order.adapter.gateway.entity.OrderItemEntity;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderHistory;
import kr.hhplus.be.server.order.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderJpaGateWay implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderHistoryRepository orderHistoryRepository;


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
}
