package kr.hhplus.be.server.orderservice.infra.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.orderservice.infra.kafka.dto.ProductInfo;
import kr.hhplus.be.server.orderservice.infra.kafka.dto.ProductRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, CompletableFuture<ProductInfo>> productFutures = new ConcurrentHashMap<>();

    public ProductInfo getProductInfo(Long productId) {
        String correlationId = java.util.UUID.randomUUID().toString();
        CompletableFuture<ProductInfo> future = new CompletableFuture<>();
        productFutures.put(correlationId, future);

        try {
            ProductRequestDto request = new ProductRequestDto(productId, correlationId);
            String jsonRequest = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("product-request", jsonRequest);
            return future.get(); // 비동기 응답 대기

        } catch (Exception e) {
            log.error("Failed to get product info", e);
            productFutures.remove(correlationId);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "product-response", groupId = "order-service-group")
    public void receiveProductInfo(@Payload String message) {
        try {
            ProductInfo productInfo = objectMapper.readValue(message, ProductInfo.class);
            CompletableFuture<ProductInfo> future = productFutures.remove(productInfo.getCorrelationId());
            if (future != null) {
                future.complete(productInfo);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize product info", e);
        }
    }

    public void reduceStock(List<ProductStockUpdate> productStockUpdates) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(productStockUpdates);
            kafkaTemplate.send("product-reduce-stock", jsonRequest);
        } catch (JsonProcessingException e) {
            log.error("Failed to send reduce stock request", e);
        }
    }

    public record ProductStockUpdate(Long productId, int quantity) {}
}