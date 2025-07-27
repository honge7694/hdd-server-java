package kr.hhplus.be.server.userservice.application.usecase.port.in;

import kr.hhplus.be.server.userservice.application.usecase.command.UserRegisterCommand;
import kr.hhplus.be.server.userservice.application.usecase.port.out.UserOutput;

public interface UserRegisterInput {
    void registerUser(UserRegisterCommand command, UserOutput output);
}
