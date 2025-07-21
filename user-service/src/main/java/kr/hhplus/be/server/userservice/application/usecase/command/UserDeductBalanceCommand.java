package kr.hhplus.be.server.userservice.application.usecase.command;

public record UserDeductBalanceCommand(Long userId, int amount) {
}
