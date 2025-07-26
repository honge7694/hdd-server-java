package kr.hhplus.be.server.orderservice.domain;

import kr.hhplus.be.server.orderservice.application.usecase.port.result.OrderData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderCompletedDataPlatformEvent extends ApplicationEvent {

    private final OrderData orderData;

    public OrderCompletedDataPlatformEvent(Object source, OrderData orderData) {
        super(source);
        this.orderData = orderData;
    }
}
