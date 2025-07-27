package kr.hhplus.be.server.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChargeBalanceRequestDto {

    @Schema(description = "유저 아이디", example = "1")
    Long userId;

    @Schema(description = "충전 금액", example = "10000")
    int amount;
}
