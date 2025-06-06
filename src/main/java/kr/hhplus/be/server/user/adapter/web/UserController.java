package kr.hhplus.be.server.user.adapter.web;

import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.user.adapter.web.docs.UserDocs;
import kr.hhplus.be.server.user.adapter.web.dto.AddressResponseDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserChargeBalanceRequestDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserRequestDto;
import kr.hhplus.be.server.user.adapter.web.dto.UserResponseDto;
import kr.hhplus.be.server.user.usecase.in.UserChargeBalanceCommand;
import kr.hhplus.be.server.user.usecase.in.UserChargeBalanceInput;
import kr.hhplus.be.server.user.usecase.in.UserRegisterCommand;
import kr.hhplus.be.server.user.usecase.in.UserRegisterInput;
import kr.hhplus.be.server.user.usecase.out.UserOutput;
import kr.hhplus.be.server.user.usecase.out.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController implements UserDocs, UserOutput {

    private final UserRegisterInput userRegisterInput;
    private final UserChargeBalanceInput userChargeBalanceInput;
    private UserResponseDto lastResponse; // Presenter로 값을 받아올 임시 변수

    @Override
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@RequestBody UserRequestDto requestDto) {
        UserRegisterCommand command = new UserRegisterCommand(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword(),
                // Address도 값 객체라면 변환 필요
                requestDto.getAddress().toDomain()
        );
        userRegisterInput.registerUser(command, this);
        ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(lastResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<UserResponseDto>> chargeUserBalance(@RequestBody UserChargeBalanceRequestDto requestDto) {
        UserChargeBalanceCommand command = new UserChargeBalanceCommand(
                requestDto.getUserId(),
                requestDto.getAmount()
        );
        userChargeBalanceInput.chargeUserBalance(command, this);
        return ResponseEntity.ok(ApiResponse.success(lastResponse));
    }

    /* Presenter 역할 */
    @Override
    public void ok(UserResult userRegisterResult) {
        lastResponse = new UserResponseDto(
                userRegisterResult.id(),
                userRegisterResult.name(),
                userRegisterResult.email(),
                userRegisterResult.balance(),
                AddressResponseDto.from(userRegisterResult.address())
        );
    }
}
