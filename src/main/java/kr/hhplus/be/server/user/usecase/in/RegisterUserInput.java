package kr.hhplus.be.server.user.usecase.in;

import kr.hhplus.be.server.user.usecase.out.RegisterUserOutput;

public interface RegisterUserInput {
    void registerUser(RegisterUserCommand command,  RegisterUserOutput output);
}
