package kr.hhplus.be.server.order.infra.event;

import kr.hhplus.be.server.order.application.usecase.port.result.OrderData;
import kr.hhplus.be.server.order.domain.OrderCompletedDataPlatformEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class DataPlatformListener {

    //private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient = WebClient.create("http://localhost:8080");

    @Async
    @EventListener
    public void handleOrderCompletedEvent(OrderCompletedDataPlatformEvent event) {
        log.info("이벤트 수신: {}", event.getOrderData().orderId());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String mockApiUrl = "http://localhost:8080/api/mock/order";

        // RestTemplate 사용
        //restTemplate.postForObject(mockApiUrl, event.getOrderData(), String.class);


        // WebClient 사용
        webClient.post()
                .uri("/api/mock/order")
                .body(Mono.just(event.getOrderData()), OrderData.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(response -> log.info("Mock API 응답: {}", response));


        log.info("Mock API로 주문 정보 전송 완료!");
    }
}
