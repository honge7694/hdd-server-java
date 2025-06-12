package kr.hhplus.be.server.user.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @Schema(description = "유저 이름", example = "홍길동")
    private String name;

    @Schema(description = "유저 이메일(중복 불가)", example = "test@naver.com")
    private String email;

    @Schema(description = "비밀번호", example = "password")
    private String password;

    @Schema(description = "주소 입력")
    private AddressRequestDto address;
}
