package kr.hhplus.be.server.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.coupon.dto.CouponRequestDto;
import kr.hhplus.be.server.coupon.dto.CouponResponseDto;
import kr.hhplus.be.server.coupon.service.CouponService;
import kr.hhplus.be.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰")
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @Operation(
            summary = "쿠폰 생성 API",
            description = "쿠폰 생성"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = io.swagger.v3.oas.annotations.responses.ApiResponse.class, example = """
                            {
                                "status": 200,
                                "message": "success",
                                {
                                    "status": 200,
                                    "message": "success",
                                    "data": {
                                        "couponId": 1,
                                        "name": "string",
                                        "code": "string",
                                        "discountAmount": 10000,
                                        "quantity": 10,
                                        "issuedAt": "2025-06-16",
                                        "expiresDate": "2025-06-16"
                                    }
                                }
                            }
                        """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 쿠폰이 존재하지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = io.swagger.v3.oas.annotations.responses.ApiResponse.class, example = """
                                {
                                    "status": 404,
                                    "message": "해당 쿠폰이 존재하지 않습니다.",
                                    "data": null
                                }
                        """))),
    })
    @PostMapping("/v1/create")
    public ResponseEntity<ApiResponse<CouponResponseDto>> createCoupon(
            @RequestBody(required = true) CouponRequestDto couponRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(couponService.createCoupon(couponRequestDto)));
    }
}
