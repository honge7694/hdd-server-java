package kr.hhplus.be.server.user.presentation;

import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.user.presentation.docs.UserDocs;
import kr.hhplus.be.server.user.presentation.dto.AddressResponseDto;
import kr.hhplus.be.server.user.presentation.dto.UserChargeBalanceRequestDto;
import kr.hhplus.be.server.user.presentation.dto.UserRequestDto;
import kr.hhplus.be.server.user.presentation.dto.UserResponseDto;
import kr.hhplus.be.server.user.application.usecase.command.UserChargeBalanceCommand;
import kr.hhplus.be.server.user.application.usecase.port.in.UserChargeBalanceInput;
import kr.hhplus.be.server.user.application.usecase.command.UserRegisterCommand;
import kr.hhplus.be.server.user.application.usecase.port.in.UserRegisterInput;
import kr.hhplus.be.server.user.application.usecase.port.out.UserOutput;
import kr.hhplus.be.server.user.application.usecase.result.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController implements UserDocs, UserOutput {

    private final UserRegisterInput userRegisterInput;
    private final UserChargeBalanceInput userChargeBalanceInput;
    private UserResponseDto lastResponse;

    @Override
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@RequestBody UserRequestDto requestDto) {
        UserRegisterCommand command = new UserRegisterCommand(
                requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword(),
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
