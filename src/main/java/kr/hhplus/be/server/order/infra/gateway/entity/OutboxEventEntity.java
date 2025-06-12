package kr.hhplus.be.server.order.infra.gateway.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.order.domain.OutboxEvent;
import kr.hhplus.be.server.order.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "outbox_event")
public class OutboxEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String type;

    @Lob
    private String payload;

    private OrderStatus status;

    private LocalDateTime createdAt;

    // 도메인 -> 엔티티 변환 메서드
    public static OutboxEventEntity fromDomain(OutboxEvent outboxEvent, Long orderId) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.orderId = orderId;
        entity.type = outboxEvent.getType();
        entity.payload = outboxEvent.getPayload();
        entity.status = outboxEvent.getStatus();
        entity.createdAt = LocalDateTime.now();
        return entity;
    }

    // 엔티티 -> 도메인 변환 메서드
    public static OutboxEvent toDomain(OutboxEventEntity entity) {
        return OutboxEvent.create(entity.getType(), entity.getPayload(), entity.getStatus());
    }
}

