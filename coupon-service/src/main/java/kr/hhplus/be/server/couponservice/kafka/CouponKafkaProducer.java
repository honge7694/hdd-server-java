package kr.hhplus.be.server.couponservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public <T> void send(String topic, T data, String correlationId) {
        log.info("CouponKafkaProducer: {}", data);
        try {
            String jsonString = objectMapper.writeValueAsString(data);

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, correlationId, jsonString);

            if(correlationId != null) {
                record.headers().add(new RecordHeader("correlationId", correlationId.getBytes()));
            }

            kafkaTemplate.send(record);
        } catch (JsonProcessingException e) {
            log.error("CouponKafkaProducer Serialize Data Fail. [correlationId={}]", correlationId, e);
        }
    }
}
