package kr.hhplus.be.server.couponservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.couponservice.dto.CouponRequestDto;
import kr.hhplus.be.server.couponservice.dto.CouponResponseDto;
import kr.hhplus.be.server.couponservice.service.CouponService;
import kr.hhplus.be.server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.couponservice.dto.CouponIssuedDto;
import kr.hhplus.be.server.couponservice.dto.CouponIssueRequestDto;
import kr.hhplus.be.server.couponservice.kafka.CouponKafkaProducer;
import java.util.UUID;

@Tag(name = "쿠폰")
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    private final CouponKafkaProducer couponKafkaProducer;

    @Operation(
            summary = "쿠폰 생성 API",
            description = "쿠폰 생성"
    )
    // ... (기존 ApiResponses 생략) ...
    @PostMapping("/v1/create")
    public ResponseEntity<ApiResponse<CouponResponseDto>> createCoupon(
            @RequestBody(required = true) CouponRequestDto couponRequestDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(couponService.createCoupon(couponRequestDto)));
    }

    @Operation(
            summary = "쿠폰 발급 API",
            description = "사용자에게 쿠폰을 발급하고 재고를 차감합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 쿠폰이 존재하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "쿠폰 재고가 소진되었습니다.")
    })
    @PostMapping("/v1/issue")
    public ResponseEntity<ApiResponse<CouponResponseDto>> issueCoupon(
            @RequestBody(required = true) CouponIssueRequestDto issueRequestDto
    ) {
        // 1. 트랜잭션 추적을 위한 Correlation ID 생성
        String correlationId = UUID.randomUUID().toString();
        issueRequestDto.setCorrelationId(correlationId);

        // 2. 서비스 호출하여 비즈니스 로직 수행
        CouponIssuedDto issuedDto = couponService.issueCoupon(issueRequestDto);

        // 3. Kafka에 이벤트 발행 (발급 요청 정보를 그대로 전달)
        couponKafkaProducer.send("coupon-issue-success", issueRequestDto, correlationId);

        // 4. 클라이언트에게 API 응답 반환
        return ResponseEntity.ok(ApiResponse.success(CouponResponseDto.from(issuedDto)));
    }
}
