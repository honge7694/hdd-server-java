package kr.hhplus.be.server.order.domain;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private int price;
    private int quantity;

    public OrderItem(Long orderId, Long productId, int price, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderItem create(Long orderId, Long productId, int price, int quantity) {
        return new OrderItem(orderId, productId, price, quantity);
    }

    public OrderItem assignId(Long orderId) {
        OrderItem orderItemWithId = new OrderItem(this.orderId, this.productId, this.price, this.quantity);
        orderItemWithId.id = orderId;
        return orderItemWithId;
    }

    /* 비즈니스 로직 */
    public int totalPrice() {
        return price * quantity;
    }

    /* getter */
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
