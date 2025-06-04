package kr.hhplus.be.server.user.adapter.web.docs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.user.adapter.web.dto.UserRequestDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/users")
@Tag(name = "유저")
public interface UserDocs {

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
                            "address": {
                                "city": "서울",
                                "street": "강남대로",
                                "zipcode": "12345"
                            }
                        }
                    }
                """)
                    )
            )
    })
    @PostMapping("/v1/register")
    ResponseEntity<kr.hhplus.be.server.global.response.ApiResponse<UserResponseDto>> registerUser(
            @RequestBody UserRequestDto requestDto
    );
}
