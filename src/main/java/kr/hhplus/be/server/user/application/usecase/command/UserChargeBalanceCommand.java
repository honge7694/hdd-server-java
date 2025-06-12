package kr.hhplus.be.server.user.application.usecase.command;

public record UserChargeBalanceCommand(
        Long userId,
        int amount
) {
}
