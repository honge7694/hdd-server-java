package kr.hhplus.be.server.orderservice.application.interactor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.orderservice.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.orderservice.application.usecase.command.ProductItemCommand;
import kr.hhplus.be.server.orderservice.application.usecase.port.in.PlaceOrderInput;
import kr.hhplus.be.server.orderservice.application.usecase.port.out.OrderRepository;
import kr.hhplus.be.server.orderservice.application.usecase.port.out.PlaceOrderOutput;
import kr.hhplus.be.server.orderservice.domain.Order;
import kr.hhplus.be.server.orderservice.domain.OrderHistory;
import kr.hhplus.be.server.orderservice.domain.OrderItem;
import kr.hhplus.be.server.orderservice.domain.OutboxEvent;
import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;
import kr.hhplus.be.server.orderservice.infra.gateway.custom.OrderRepositoryCustom;
import kr.hhplus.be.server.orderservice.infra.kafka.ProductServiceClient;
import kr.hhplus.be.server.orderservice.infra.kafka.UserServiceClient;
import kr.hhplus.be.server.orderservice.infra.kafka.dto.ProductInfo;
import kr.hhplus.be.server.orderservice.infra.kafka.dto.UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceOrderInteractor implements PlaceOrderInput {

    private final OrderRepository orderRepository; // for OrderItem, OrderHistory
    private final OrderRepositoryCustom orderRepositoryCustom; // for Order + OutboxEvent
    private final ProductServiceClient productServiceClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void orderItemCommand(PlaceOrderCommand command, PlaceOrderOutput present) {
        // 1. 상품 정보 조회 및 재고 확인
        Map<Long, ProductInfo> productInfoMap = validateProducts(command);

        // 2. Outbox 이벤트 생성
        OutboxEvent outboxEvent = createOrderOutboxEvent(command, productInfoMap);

        // 3. 주문 생성 및 저장 (Outbox 이벤트 포함)
        createAndSaveOrder(command, productInfoMap, outboxEvent);

        // 4. 결과 반환
        present.ok(null);
    }

    /**
     * 사용자 정보를 검증하고, 주문할 상품들의 정보를 조회하여 재고 및 최종 가격을 확인합니다.
     */
    private Map<Long, ProductInfo> validateProducts(PlaceOrderCommand command) {
        List<Long> productIds = command.items().stream()
                .map(ProductItemCommand::productId)
                .collect(Collectors.toList());

        Map<Long, ProductInfo> productInfoMap = productIds.stream()
                .collect(Collectors.toMap(id -> id, productServiceClient::getProductInfo));

        for (ProductItemCommand itemCommand : command.items()) {
            ProductInfo productInfo = productInfoMap.get(itemCommand.productId());
            if (productInfo == null) {
                throw new NotFoundException("해당 상품이 존재하지 않습니다. (상품 ID: " + itemCommand.productId() + ")");
            }
            if (productInfo.getStock() < itemCommand.quantity()) {
                throw new ConflictException("상품 재고가 부족합니다. (상품 ID: " + itemCommand.productId() + ")");
            }
        }

        return productInfoMap;
    }

    /**
     * 주문(Order, OrderItem, OrderHistory)을 생성하고 데이터베이스에 저장합니다.
     * Outbox 이벤트를 함께 저장하여 트랜잭션의 원자성을 보장합니다.
     */
    private void createAndSaveOrder(PlaceOrderCommand command, Map<Long, ProductInfo> productInfoMap, OutboxEvent outboxEvent) {
        Order order = Order.create(command.userId(), 0L, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Order savedOrder = orderRepositoryCustom.save(order, outboxEvent); // Order와 OutboxEvent를 함께 저장

        for (ProductItemCommand itemCommand : command.items()) {
            ProductInfo productInfo = productInfoMap.get(itemCommand.productId());
            OrderItem orderItem = OrderItem.create(savedOrder.getId(), itemCommand.productId(), productInfo.getPrice(), itemCommand.quantity());
            orderRepository.save(orderItem);
        }

        OrderHistory orderHistory = OrderHistory.create(savedOrder.getId(), OrderStatus.PENDING, LocalDateTime.now());
        orderRepository.save(orderHistory);
    }

    /**
     * 주문 완료에 대한 Outbox 이벤트를 생성합니다.
     */
    private OutboxEvent createOrderOutboxEvent(PlaceOrderCommand command, Map<Long, ProductInfo> productInfoMap) {
        try {
            String correlationId = UUID.randomUUID().toString();
            int totalPrice = command.items().stream()
                    .mapToInt(item -> productInfoMap.get(item.productId()).getPrice() * item.quantity())
                    .sum();

            Map<String, Object> payload = Map.of(
                    "userId", command.userId(),
                    "orderId", 0L, // This will be updated after order is saved
                    "totalPrice", totalPrice,
                    "items", command.items().stream()
                            .map(item -> Map.of("productId", item.productId(), "quantity", item.quantity(), "price", productInfoMap.get(item.productId()).getPrice()))
                            .collect(Collectors.toList()),
                    "status", OrderStatus.PENDING.toString(),
                    "correlationId", correlationId
            );
            String jsonPayload = objectMapper.writeValueAsString(payload);
            return OutboxEvent.create("ORDER_PENDING", jsonPayload, OrderStatus.PENDING);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order event payload for command: {}", command, e);
            throw new RuntimeException("Failed to create outbox event payload", e);
        }
    }
}


