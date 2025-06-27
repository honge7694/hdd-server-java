package kr.hhplus.be.server.order.application.interactor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.order.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.order.application.usecase.command.ProductItemCommand;
import kr.hhplus.be.server.order.application.usecase.port.in.PlaceOrderInput;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderIdempotencyRepository;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderMessageRepository;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderRepository;
import kr.hhplus.be.server.order.application.usecase.port.out.PlaceOrderOutput;
import kr.hhplus.be.server.order.application.usecase.port.result.PlaceOrderResult;
import kr.hhplus.be.server.order.application.usecase.port.result.dto.ProductItem;
import kr.hhplus.be.server.order.domain.*;
import kr.hhplus.be.server.order.domain.enums.OrderStatus;
import kr.hhplus.be.server.product.model.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.usercoupon.repository.UserCouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlaceOrderInteractor implements PlaceOrderInput {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMessageRepository orderMessageRepository;
    private final ObjectMapper objectMapper;
    private final OrderIdempotencyRepository orderIdempotencyRepository;
    private final UserCouponRepository userCouponRepository;
    private final IdempotencyService idempotencyService;
    private final CacheManager cacheManager;

    public PlaceOrderInteractor(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, OrderMessageRepository orderMessageRepository, ObjectMapper objectMapper, OrderIdempotencyRepository orderIdempotencyRepository, UserCouponRepository userCouponRepository, IdempotencyService idempotencyService, CacheManager cacheManager) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderMessageRepository = orderMessageRepository;
        this.objectMapper = objectMapper;
        this.orderIdempotencyRepository = orderIdempotencyRepository;
        this.userCouponRepository = userCouponRepository;
        this.idempotencyService = idempotencyService;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional
    public void orderItemCommand(PlaceOrderCommand placeOrderCommand, PlaceOrderOutput present) throws JsonProcessingException {
        // 캐시
        Cache productCache = cacheManager.getCache("products");

        User user = userRepository.findById(placeOrderCommand.userId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        // Order 생성
        Long todoCouponId = 0L;
        Order order = Order.create(user.getId(), todoCouponId, OrderStatus.ORDERED, LocalDateTime.now(), LocalDateTime.now());
        //Order savedOrder = orderRepository.save(order);
        // 메시징(Outbox)
        Order savedOrder = outBoxMessaging(order, placeOrderCommand.items());

        // OrderItem 생성
        int totalPrice = 0;
        List<Product> productItems = new ArrayList<>();
        for(ProductItemCommand productItemCommand : placeOrderCommand.items()) {
            Long productId = productItemCommand.productId();
//            Product product = productRepository.findByWithLock(productId)
//                    .orElseThrow(() -> new NotFoundException("해당 상품이 존재하지 않습니다. (상품 ID : " + productId + ")"));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("해당 상품이 존재하지 않습니다. (상품 ID : " + productId + ")"));
            product.reduceStock(productItemCommand.quantity());

            // 상품 캐시 제거
            if (productCache != null) {
                try {
                    productCache.evict(productId);
                    log.info("products::{} 캐시 제거", productId);
                } catch (Exception e) {
                    log.warn("products::{} 캐시 제거 중 에러 발생", productId);
                }
            }

            totalPrice += product.getPrice() * productItemCommand.quantity();

            productItems.add(product);

            OrderItem orderItem = OrderItem.create(savedOrder.getId(), productId, product.getPrice(), productItemCommand.quantity());
            orderRepository.save(orderItem);
        }

        // 유저 포인트 계산
        user.deductBalance(totalPrice);
        userRepository.update(user);

        // OrderHistory 생성
        OrderHistory orderHistory = OrderHistory.create(savedOrder.getId(), OrderStatus.ORDERED, LocalDateTime.now());
        orderRepository.save(orderHistory);

        present.ok(
                new PlaceOrderResult(
                        savedOrder.getId(),
                        user.getId(),
                        OrderStatus.ORDERED,
                        productItems.stream().map(product -> {
                            int quantity = placeOrderCommand.items().stream()
                                    .filter(cmd -> cmd.productId().equals(product.getId()))
                                    .findFirst()
                                    .map(ProductItemCommand::quantity)
                                    .orElse(0);

                            return new ProductItem(
                                    product.getId(),
                                    product.getName(),
                                    product.getPrice(),
                                    quantity
                            );
                        }).collect(Collectors.toList()),
                        totalPrice,
                        savedOrder.getCreatedAt()
                ));
    }

    private Order outBoxMessaging(Order order, List<ProductItemCommand> productItems) throws JsonProcessingException {
        OrderCreatePayload payload = OrderCreatePayload.create(order.getUserId(), order.getUserCouponId(), productItems);
        String jsonPayload = objectMapper.writeValueAsString(payload);
        OutboxEvent event = new OutboxEvent("order_created", jsonPayload, OrderStatus.PAID);
        return orderMessageRepository.save(order, event);
    }
}
