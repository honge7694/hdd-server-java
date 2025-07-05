package kr.hhplus.be.server.usercoupon.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.coupon.dto.CouponRequestDto;
import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.usercoupon.dto.UserCouponRequestDto;
import kr.hhplus.be.server.usercoupon.dto.UserCouponResponseDto;
import kr.hhplus.be.server.usercoupon.facade.UserCouponLockFacade;
import kr.hhplus.be.server.usercoupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 쿠폰")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/coupon")
public class UserCouponController {

    private final UserCouponService userCouponService;
    private final UserCouponLockFacade userCouponLockfacade;

    @Operation(
            summary = "유저 쿠폰 등록 API",
            description = "쿠폰 등록"
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
                                        "id": 1,
                                        "userId": 202,
                                        "couponId": 1,
                                        "used": false,
                                        "createdAt": "2025-06-17"
                                    }
                                }
                            }
                        """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저가 존재하지 않습니다.",
                    content = @Content(
                            schema = @Schema(implementation = io.swagger.v3.oas.annotations.responses.ApiResponse.class, example = """
                                {
                                    "status": 404,
                                    "message": "유저가 존재하지 않습니다.",
                                    "data": null
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 해당 쿠폰을 발급받은 사용자입니다.",
                    content = @Content(
                            schema = @Schema(implementation = io.swagger.v3.oas.annotations.responses.ApiResponse.class, example = """
                                {
                                    "status": 409,
                                    "message": "이미 해당 쿠폰을 발급받은 사용자입니다.",
                                    "data": null
                                }
                        """))),
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserCouponResponseDto>> getUserCoupon(
            @RequestBody UserCouponRequestDto userCouponRequestDto
    ) throws InterruptedException {
//        return ResponseEntity.ok(ApiResponse.success(userCouponService.registerUserCoupon(userCouponRequestDto)));
        return ResponseEntity.ok(ApiResponse.success(userCouponLockfacade.registerUserCouponLock(userCouponRequestDto)));
    }

    @Operation(
            summary = "유저 쿠폰 등록 API",
            description = "쿠폰 등록"
    )
    @PostMapping("/v1/queue")
    public ResponseEntity<ApiResponse<String>> requestCoupon(@RequestBody UserCouponRequestDto userCouponRequestDto) {
        userCouponService.queueCouponRequest(userCouponRequestDto.getUserId(), userCouponRequestDto.getCouponId());
        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급이 완료되었습니다."));
    }
}
