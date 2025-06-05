package kr.hhplus.be.server.user.adapter.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.user.adapter.web.dto.UserChargeBalanceRequestDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserRequestDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
@Tag(name = "유저")
public interface UserDocs {

    @Operation(
            summary = "유저 생성 API",
            description = "신규 유저를 생성합니다."
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
                            "id": 1,
                            "name": "홍길동",
                            "email": "hong@sample.com",
                            "balance: 0,
                            "address": {
                                "city": "서울",
                                "street": "강남대로",
                                "zipcode": "12345"
                            }
                        }
                    }
                """))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일입니다.",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class,
                            example = """
                                {
                                    "status": 409,
                                    "message": "이미 사용 중인 이메일입니다.",
                                    "data": null
                                }
                        """))),
    })
    @PostMapping("/v1/register")
    ResponseEntity<kr.hhplus.be.server.global.response.ApiResponse<UserResponseDto>> registerUser(
            @RequestBody UserRequestDto requestDto
    );

    @Operation(
            summary = "유저 포인트 충전 API",
            description = "유저의 포인트를 충전합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "충전 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "status": 200,
                        "message": "success",
                        "data": {
                            "id": 1,
                            "name": "홍길동",
                            "email": "hong@sample.com",
                            "balance": 1000,
                            "address": {
                                "city": "서울",
                                "street": "강남대로",
                                "zipcode": "12345"
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
    })
    @PostMapping("/v1/charge")
    ResponseEntity<kr.hhplus.be.server.global.response.ApiResponse<UserResponseDto>> chargeUserBalance(
            @RequestBody UserChargeBalanceRequestDto requestDto
    );
}
