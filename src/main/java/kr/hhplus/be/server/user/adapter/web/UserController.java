package kr.hhplus.be.server.user.adapter.web;

import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.user.adapter.web.docs.UserDocs;
import kr.hhplus.be.server.user.adapter.web.dto.AddressResponseDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserRequestDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserResponseDto;
import kr.hhplus.be.server.user.usecase.in.RegisterUserCommand;
import kr.hhplus.be.server.user.usecase.in.RegisterUserInput;
import kr.hhplus.be.server.user.usecase.out.RegisterUserOutput;
import kr.hhplus.be.server.user.usecase.out.RegisterUserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class UserController implements UserDocs, RegisterUserOutput {

    private final RegisterUserInput registerUserInput;
    private UserResponseDto lastResponse; // Presenter로 값을 받아올 임시 변수

    @Override
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@RequestBody UserRequestDto requestDto) {
        System.out.println("requestDto = " + requestDto.getName() + requestDto.getEmail());
        System.out.println("requestDto.getAddress() = " + requestDto.getAddress());
        RegisterUserCommand command = new RegisterUserCommand(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword(),
                // Address도 값 객체라면 변환 필요
                requestDto.getAddress().toDomain()
        );
        registerUserInput.registerUser(command, this);
        ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(lastResponse);
        return ResponseEntity.ok(apiResponse);
    }

    /* Presenter 역할 */
    @Override
    public void ok(RegisterUserResult registerUserResult) {
        lastResponse = new UserResponseDto(
                registerUserResult.id(),
                registerUserResult.name(),
                registerUserResult.email(),
                AddressResponseDto.from(registerUserResult.address())
        );
    }
}
