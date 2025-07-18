package kr.hhplus.be.server.productservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.productservice.kafka.dto.ProductRequestDto;
import kr.hhplus.be.server.productservice.product.dto.ProductResponseDto;
import kr.hhplus.be.server.productservice.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaListener {

    private final ProductService productService;
    private final ProductKafkaProducer productKafkaProducer;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product-request", groupId = "product-group")
    public void getProduct(String message) {
        log.info("product-request message: {}", message);

        String correlationId = null;

        try {
            ProductRequestDto request = objectMapper.readValue(message, ProductRequestDto.class);
            correlationId = request.getCorrelationId();
            ProductResponseDto productResponseDto = productService.getProduct(request.getProductId());
            productKafkaProducer.send("product-response", productResponseDto, correlationId);
        } catch (IOException e) {
            log.error("product-response fail. [correlationId={}]", correlationId, e);
        }
    }
}