package kr.hhplus.be.server.user.usecase.in;

import kr.hhplus.be.server.user.usecase.out.UserOutput;

public interface UserRegisterInput {
    void registerUser(UserRegisterCommand command, UserOutput output);
}
