package kr.hhplus.be.server.user.application.usecase.port.out;

import kr.hhplus.be.server.user.application.usecase.result.UserResult;

public interface UserOutput {
    void ok(UserResult registerUserResult);
}
