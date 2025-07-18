package kr.hhplus.be.server.mock.controller;

import kr.hhplus.be.server.order.application.usecase.port.result.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/mock")
public class MockOrderApiController {

    @PostMapping("/order")
    public String receiveMockOrder(@RequestBody OrderData orderData) {
        log.info("Mock API가 호출되었습니다.");
        log.info("Order ID : {}", orderData.orderId());
        log.info("Product : {}", orderData.productList().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
        return "SUCCESS";
    }
}
