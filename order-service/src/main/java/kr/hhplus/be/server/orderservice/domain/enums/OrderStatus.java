package kr.hhplus.be.server.orderservice.domain.enums;

public enum OrderStatus {
    ORDERED,
    PAID,
    PENDING,
    READY_FOR_SHIPMENT,
    SHIPPING,
    DELIVERED,
    CANCELED,
    RETURN_REQUESTED,
    REFUNDED,
    COMPLETED,
    FAILED
}
