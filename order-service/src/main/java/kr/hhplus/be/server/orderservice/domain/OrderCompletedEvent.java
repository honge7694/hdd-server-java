package kr.hhplus.be.server.orderservice.domain;

import java.util.List;

public record OrderCompletedEvent(
        List<Long> productIds
) {
}
