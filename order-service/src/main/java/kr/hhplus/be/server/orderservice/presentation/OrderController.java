package kr.hhplus.be.server.orderservice.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.orderservice.application.interactor.PlaceOrderFacade;
import kr.hhplus.be.server.orderservice.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.orderservice.application.usecase.port.in.PlaceOrderInput;
import kr.hhplus.be.server.orderservice.application.usecase.port.out.PlaceOrderOutput;
import kr.hhplus.be.server.orderservice.application.usecase.port.result.PlaceOrderResult;
import kr.hhplus.be.server.orderservice.presentation.docs.OrderDocs;
import kr.hhplus.be.server.orderservice.presentation.dto.OrderRequestDto;
import kr.hhplus.be.server.orderservice.presentation.dto.OrderResponseDto;
import kr.hhplus.be.server.orderservice.presentation.dto.ProductRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderDocs, PlaceOrderOutput {

    private final PlaceOrderInput placeOrderInput;
    private final PlaceOrderFacade placeOrderFacade;
    private OrderResponseDto lastResponse;

    @Override
    public ResponseEntity<ApiResponse<OrderResponseDto>> placeOrder(@RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
        PlaceOrderCommand command = new PlaceOrderCommand(
                orderRequestDto.getUserId(),
                orderRequestDto.getUserCouponId(),
                orderRequestDto.getIdempotencyKey(),
                orderRequestDto.getProductItems().stream()
                        .map(ProductRequestDto::toDomain)
                        .collect(Collectors.toList())
        );

//        placeOrderInput.orderItemCommand(command, this);
        placeOrderFacade.orderWithRetry(command, this);
        return ResponseEntity.ok(ApiResponse.success(lastResponse));
    }

    /* Presenter 역할 */
    @Override
    public void ok(PlaceOrderResult placeOrderResult) {
        lastResponse = new OrderResponseDto(
            placeOrderResult.id(),
            placeOrderResult.userId(),
            placeOrderResult.status(),
            placeOrderResult.productList(),
            placeOrderResult.totalPrice(),
            placeOrderResult.createdAt()
        );
    }
}

