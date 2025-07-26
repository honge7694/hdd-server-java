package kr.hhplus.be.server.orderservice.infra.kafka;

import kr.hhplus.be.server.orderservice.domain.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(OutboxEvent event) {
        kafkaTemplate.send(event.getTopic(), event.getPayload());
    }
}
