package kr.hhplus.be.server.orderservice.infra.kafka;

import kr.hhplus.be.server.orderservice.domain.OutboxEvent;
import kr.hhplus.be.server.orderservice.infra.gateway.custom.OutboxRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventScheduler {

    private final OutboxRepositoryCustom outboxRepository;
    private final OrderKafkaProducer orderKafkaProducer;

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findTop100ByOrderByCreatedAtAsc();
        for (OutboxEvent event : events) {
            orderKafkaProducer.send(event);
            outboxRepository.delete(event);
        }
    }
}
