package kr.hhplus.be.server.user.usecase.in;

import kr.hhplus.be.server.user.usecase.out.UserOutput;

public interface UserChargeBalanceInput {
    void chargeUserBalance(UserChargeBalanceCommand command, UserOutput output);
}
