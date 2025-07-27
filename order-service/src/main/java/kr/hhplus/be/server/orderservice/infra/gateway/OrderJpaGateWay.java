package kr.hhplus.be.server.orderservice.infra.gateway;

import kr.hhplus.be.server.orderservice.application.usecase.port.out.OrderIdempotencyRepository;
import kr.hhplus.be.server.orderservice.application.usecase.port.out.OrderMessageRepository;
import kr.hhplus.be.server.orderservice.application.usecase.port.out.OrderRepository;
import kr.hhplus.be.server.orderservice.domain.*;
import kr.hhplus.be.server.orderservice.infra.gateway.custom.OrderRepositoryCustom;
import kr.hhplus.be.server.orderservice.infra.gateway.custom.OutboxRepositoryCustom;
import kr.hhplus.be.server.orderservice.infra.gateway.entity.OrderEntity;
import kr.hhplus.be.server.orderservice.infra.gateway.entity.OrderHistoryEntity;
import kr.hhplus.be.server.orderservice.infra.gateway.entity.OrderIdempotencyEntity;
import kr.hhplus.be.server.orderservice.infra.gateway.entity.OrderItemEntity;
import kr.hhplus.be.server.orderservice.infra.gateway.jpa.OrderHistoryJpaRepository;
import kr.hhplus.be.server.orderservice.infra.gateway.jpa.OrderIdempotencyJpaRepository;
import kr.hhplus.be.server.orderservice.infra.gateway.jpa.OrderItemJpaRepository;
import kr.hhplus.be.server.orderservice.infra.gateway.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderJpaGateWay implements OrderRepository, OrderMessageRepository, OrderIdempotencyRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderHistoryJpaRepository orderHistoryRepository;
    //    @Qualifier("orderRepositoryCustomImpl")
    private final OrderRepositoryCustom orderRepositoryCustom;
    private final OutboxRepositoryCustom outboxRepositoryCustom;
    private final OrderIdempotencyJpaRepository orderIdempotencyJpaRepository;

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
    public int findAllSize() {
        return orderJpaRepository.findAll().size();
    }

    @Override
    public void saveOutboxEvent(OutboxEvent event) {

    }

    @Override
    public OutboxEvent findByOrderId(Long orderId) {
        return outboxRepositoryCustom.findByOrderId(orderId);
    }

    @Override
    public OrderIdempotency save(OrderIdempotency orderIdempotency) {
        OrderIdempotencyEntity orderIdempotencyEntity = OrderIdempotencyEntity.fromDomain(orderIdempotency);
        OrderIdempotencyEntity savedOrderIdempotencyEntity = orderIdempotencyJpaRepository.save(orderIdempotencyEntity);
        return savedOrderIdempotencyEntity.toDomain();
    }

    @Override
    public Optional<OrderIdempotency> findByIdempotencyKey(String idempotencyKey) {
        return orderIdempotencyJpaRepository
                .findByIdempotencyKey(idempotencyKey)
                .map(OrderIdempotencyEntity::toDomain);
    }

    @Override
    public void saveAndFlush(OrderIdempotency orderIdempotency) {
        OrderIdempotencyEntity orderIdempotencyEntity = OrderIdempotencyEntity.fromDomain(orderIdempotency);
        orderIdempotencyJpaRepository.saveAndFlush(orderIdempotencyEntity);
    }
}
