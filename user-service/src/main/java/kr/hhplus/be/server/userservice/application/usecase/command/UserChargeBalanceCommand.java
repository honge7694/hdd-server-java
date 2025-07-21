package kr.hhplus.be.server.userservice.application.usecase.command;

public record UserChargeBalanceCommand(
        Long userId,
        int amount
) {
}
