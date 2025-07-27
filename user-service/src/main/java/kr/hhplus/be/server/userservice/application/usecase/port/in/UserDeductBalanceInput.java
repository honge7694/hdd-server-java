package kr.hhplus.be.server.userservice.application.usecase.port.in;

import kr.hhplus.be.server.userservice.application.usecase.command.UserDeductBalanceCommand;

public interface UserDeductBalanceInput {
    void deductUserBalance(UserDeductBalanceCommand command);
}
