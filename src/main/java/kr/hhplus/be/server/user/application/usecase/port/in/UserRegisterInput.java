package kr.hhplus.be.server.user.application.usecase.port.in;

import kr.hhplus.be.server.user.application.usecase.command.UserRegisterCommand;
import kr.hhplus.be.server.user.application.usecase.port.out.UserOutput;

public interface UserRegisterInput {
    void registerUser(UserRegisterCommand command, UserOutput output);
}
