package kr.hhplus.be.server.user.usecase.in;

public record UserChargeBalanceCommand(
        Long userId,
        int amount
) {
}
