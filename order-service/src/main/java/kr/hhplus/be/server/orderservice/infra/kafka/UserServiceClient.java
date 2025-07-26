package kr.hhplus.be.server.orderservice.infra.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.orderservice.infra.kafka.dto.UserInfo;
import kr.hhplus.be.server.orderservice.infra.kafka.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, CompletableFuture<UserInfo>> userFutures = new ConcurrentHashMap<>();

    public UserInfo getUserInfo(Long userId) {
        String correlationId = java.util.UUID.randomUUID().toString();
        CompletableFuture<UserInfo> future = new CompletableFuture<>();
        userFutures.put(correlationId, future);

        try {
            UserRequestDto request = new UserRequestDto(userId, correlationId);
            String jsonRequest = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("user-request", jsonRequest);
            return future.get(); // 비동기 응답 대기
        } catch (Exception e) {
            log.error("Failed to get user info", e);
            userFutures.remove(correlationId);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "user-response", groupId = "order-service-group")
    public void receiveUserInfo(@Payload String message) {
        try {
            UserInfo userInfo = objectMapper.readValue(message, UserInfo.class);
            CompletableFuture<UserInfo> future = userFutures.remove(userInfo.getCorrelationId());
            if (future != null) {
                future.complete(userInfo);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize user info", e);
        }
    }

    public void deductBalance(UserBalanceUpdate userBalanceUpdate) {
        try {
            String jsonRequest = objectMapper.writeValueAsString(userBalanceUpdate);
            kafkaTemplate.send("user-deduct-balance", jsonRequest);
        } catch (JsonProcessingException e) {
            log.error("Failed to send deduct balance request", e);
        }
    }

    public record UserBalanceUpdate(Long userId, int amount) {}
}