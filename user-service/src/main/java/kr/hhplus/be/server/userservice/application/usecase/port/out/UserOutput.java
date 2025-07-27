package kr.hhplus.be.server.userservice.application.usecase.port.out;

import kr.hhplus.be.server.userservice.application.usecase.result.UserResult;

public interface UserOutput {
    void ok(UserResult registerUserResult);
}
