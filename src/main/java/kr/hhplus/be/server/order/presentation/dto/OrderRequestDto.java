package kr.hhplus.be.server.order.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @Schema(description = "유저 회원 번호", example = "1")
    Long userId;

    @Schema(description = "유저 쿠폰 번호", example = "0")
    Long userCouponId;

    @Schema(description = "주문 번호", example = "aaa-123")
    String idempotencyKey;

    @Schema(description = "구매 상품")
    List<ProductRequestDto> productItems;
}
