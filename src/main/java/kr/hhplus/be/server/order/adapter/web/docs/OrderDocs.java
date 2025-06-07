package kr.hhplus.be.server.order.adapter.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.order.adapter.web.dto.OrderRequestDto;
import kr.hhplus.be.server.order.adapter.web.dto.OrderResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/orders")
@Tag(name = "주문")
public interface OrderDocs {

    @Operation(
            summary = "주문 생성 API",
            description = "신규 주문을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "status": 200,
                        "message": "success",
                        "data": {
                            "userId": 1,
                            "userCouponId": 1,
                            "orderItems": {
                                "productId": 1,
                                "quantity": 1,
                            }
                        }
                    }
                """))),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class,
                                    example = """
                                {
                                    "status": 404,
                                    "message": "유저를 찾을 수 없습니다.",
                                    "data": null
                                }
                        """))),
            @ApiResponse(responseCode = "404", description = "해당 상품이 존재하지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class,
                                    example = """
                                {
                                    "status": 404,
                                    "message": "해당 상품이 존재하지 않습니다. (상품 ID : " + productId + ")",
                                    "data": null
                                }
                        """))),
            @ApiResponse(responseCode = "409", description = "잔액이 부족합니다.",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class,
                                    example = """
                                {
                                    "status": 409,
                                    "message": "잔액이 부족합니다.",
                                    "data": null
                                }
                        """))),
            @ApiResponse(responseCode = "409", description = "상품의 개수가 부족합니다.",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class,
                                    example = """
                                {
                                    "status": 409,
                                    "message": "상품의 개수가 부족합니다.",
                                    "data": null
                                }
                        """))),
    })
    @PostMapping("/v1/orderplace")
    ResponseEntity<kr.hhplus.be.server.global.response.ApiResponse<OrderResponseDto>> placeOrder(
            @RequestBody OrderRequestDto orderRequestDto
    );
}
