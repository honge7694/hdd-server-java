package kr.hhplus.be.server.user.usecase.in;

public record ChargeUserBalanceCommand(
        Long userId,
        int amount
) {
}
