package kr.hhplus.be.server.user.application.usecase.command;

import kr.hhplus.be.server.user.domain.Address;

public record UserRegisterCommand(
        String name,
        String email,
        String password,
        Address address
) {}

