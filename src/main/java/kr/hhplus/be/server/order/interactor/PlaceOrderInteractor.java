package kr.hhplus.be.server.order.interactor;

import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.order.adapter.gateway.OrderRepository;
import kr.hhplus.be.server.order.domain.Order;
import kr.hhplus.be.server.order.domain.OrderHistory;
import kr.hhplus.be.server.order.domain.OrderItem;
import kr.hhplus.be.server.order.domain.enums.OrderStatus;
import kr.hhplus.be.server.order.usecase.in.ProductItemCommand;
import kr.hhplus.be.server.order.usecase.in.PlaceOrderCommand;
import kr.hhplus.be.server.order.usecase.in.PlaceOrderInput;
import kr.hhplus.be.server.order.usecase.out.PlaceOrderOutput;
import kr.hhplus.be.server.product.model.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PlaceOrderInteractor implements PlaceOrderInput {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public PlaceOrderInteractor(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void orderItemCommand(PlaceOrderCommand placeOrderCommand, PlaceOrderOutput placeOrderOutput) {
        User user = userRepository.findById(placeOrderCommand.userId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        // Order 생성
        Long todoCouponId = 0L;
        Order order = Order.create(user.getId(), todoCouponId, OrderStatus.ORDERED, LocalDateTime.now(), LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // OrderItem 생성
        int totalPrice = 0;
        for(ProductItemCommand productItemCommand : placeOrderCommand.items()) {
            Long productId = productItemCommand.productId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("해당 상품이 존재하지 않습니다. (상품 ID : " + productId + ")"));
            product.reduceStock(productItemCommand.quantity());
            totalPrice += product.getPrice() * productItemCommand.quantity();
            productRepository.save(product);

            OrderItem orderItem = OrderItem.create(savedOrder.getId(), productId, product.getPrice(), productItemCommand.quantity());
            orderRepository.save(orderItem);
        }

        // 유저 포인트 계산
        user.deductBalance(totalPrice);
        userRepository.update(user);

        // OrderHistory 생성
        OrderHistory orderHistory = OrderHistory.create(savedOrder.getId(), OrderStatus.ORDERED, LocalDateTime.now());
        orderRepository.save(orderHistory);
    }
}
