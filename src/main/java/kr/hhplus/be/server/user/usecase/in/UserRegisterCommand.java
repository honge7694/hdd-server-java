package kr.hhplus.be.server.user.usecase.in;

import kr.hhplus.be.server.user.domain.Address;

public record UserRegisterCommand(
        String name,
        String email,
        String password,
        Address address
) {}

