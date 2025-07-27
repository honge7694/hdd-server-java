package kr.hhplus.be.server.usercouponservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.usercouponservice.dto.UserCouponResponseDto;
import kr.hhplus.be.server.usercouponservice.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "유저 쿠폰")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-coupons")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @Operation(
            summary = "유저 쿠폰 목록 조회 API",
            description = "특정 유저가 보유한 모든 쿠폰 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = List.class)
                    )
            )
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<UserCouponResponseDto>>> getUserCouponsByUserId(@PathVariable Long userId) {
        List<UserCouponResponseDto> userCoupons = userCouponService.getUserCouponsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(userCoupons));
    }
}
