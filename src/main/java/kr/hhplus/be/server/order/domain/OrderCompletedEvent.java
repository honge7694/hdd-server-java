package kr.hhplus.be.server.order.domain;

import java.util.List;

public record OrderCompletedEvent(
        List<Long> productIds
) {
}
