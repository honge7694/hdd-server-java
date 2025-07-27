package kr.hhplus.be.server.orderservice.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;
import kr.hhplus.be.server.orderservice.infra.gateway.custom.OrderRepositoryCustom;
import kr.hhplus.be.server.orderservice.infra.kafka.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaListener {

    private final OrderRepositoryCustom orderRepositoryCustom;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "PAYMENT_SUCCESSFUL", groupId = "order-service-group")
    public void handlePaymentSuccessful(String message) {
        log.info("PAYMENT_SUCCESSFUL message: {}", message);
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            orderRepositoryCustom.updateOrderStatus(event.getOrderId(), OrderStatus.COMPLETED);
            log.info("Order {} status updated to COMPLETED after successful payment.", event.getOrderId());
        } catch (IOException e) {
            log.error("Failed to process PAYMENT_SUCCESSFUL event", e);
        }
    }

    @KafkaListener(topics = "PAYMENT_FAILED", groupId = "order-service-group")
    public void handlePaymentFailed(String message) {
        log.info("PAYMENT_FAILED message: {}", message);
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            orderRepositoryCustom.updateOrderStatus(event.getOrderId(), OrderStatus.FAILED);
            log.warn("Order {} status updated to FAILED after payment failure.", event.getOrderId());
            // TODO: Compensation logic if needed (e.g., notify user, trigger refund)
        } catch (IOException e) {
            log.error("Failed to process PAYMENT_FAILED event", e);
        }
    }

    @KafkaListener(topics = "STOCK_DECREASED", groupId = "order-service-group")
    public void handleStockDecreased(String message) {
        log.info("STOCK_DECREASED message: {}", message);
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            // If payment was successful, stock decreased means order is completed
            orderRepositoryCustom.updateOrderStatus(event.getOrderId(), OrderStatus.COMPLETED);
            log.info("Order {} status updated to COMPLETED after stock decreased.", event.getOrderId());

            // 주문 완료 이벤트 발행
            kafkaTemplate.send("ORDER_COMPLETED", event);
            log.info("ORDER_COMPLETED event published for order {}.", event.getOrderId());
        } catch (IOException e) {
            log.error("Failed to process STOCK_DECREASED event", e);
        }
    }

    @KafkaListener(topics = "STOCK_DECREASE_FAILED", groupId = "order-service-group")
    public void handleStockDecreaseFailed(String message) {
        log.info("STOCK_DECREASE_FAILED message: {}", message);
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            orderRepositoryCustom.updateOrderStatus(event.getOrderId(), OrderStatus.FAILED);
            log.warn("Order {} status updated to FAILED after stock decrease failure.", event.getOrderId());
            // TODO: Compensation logic if needed (e.g., trigger payment refund)
        } catch (IOException e) {
            log.error("Failed to process STOCK_DECREASE_FAILED event", e);
        }
    }
}
