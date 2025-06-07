package kr.hhplus.be.server.order.adapter.web;

import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.order.adapter.web.docs.OrderDocs;
import kr.hhplus.be.server.order.adapter.web.dto.OrderRequestDto;
import kr.hhplus.be.server.order.adapter.web.dto.OrderResponseDto;
import kr.hhplus.be.server.order.adapter.web.dto.ProductRequestDto;
import kr.hhplus.be.server.order.usecase.in.PlaceOrderCommand;
import kr.hhplus.be.server.order.usecase.in.PlaceOrderInput;
import kr.hhplus.be.server.order.usecase.out.PlaceOrderOutput;
import kr.hhplus.be.server.order.usecase.out.PlaceOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderDocs, PlaceOrderOutput {

    private final PlaceOrderInput placeOrderInput;
    private OrderResponseDto lastResponse;

    @Override
    public ResponseEntity<ApiResponse<OrderResponseDto>> placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
        PlaceOrderCommand command = new PlaceOrderCommand(
                orderRequestDto.getUserId(),
                orderRequestDto.getUserCouponId(),
                orderRequestDto.getProductItems().stream()
                        .map(ProductRequestDto::toDomain)
                        .collect(Collectors.toList())
        );

        placeOrderInput.orderItemCommand(command, this);
        return ResponseEntity.ok(ApiResponse.success(lastResponse));
    }

    /* Presenter 역할 */
    @Override
    public void ok(PlaceOrderResult placeOrderResult) {
        lastResponse = new OrderResponseDto(
            placeOrderResult.id(),
            placeOrderResult.status(),
            placeOrderResult.createdAt()
        );
    }
}
