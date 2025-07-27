package kr.hhplus.be.server.userservice.application.usecase.port.in;

import kr.hhplus.be.server.userservice.application.usecase.command.UserChargeBalanceCommand;
import kr.hhplus.be.server.userservice.application.usecase.port.out.UserOutput;

public interface UserChargeBalanceInput {
    void chargeUserBalance(UserChargeBalanceCommand command, UserOutput output);
}
