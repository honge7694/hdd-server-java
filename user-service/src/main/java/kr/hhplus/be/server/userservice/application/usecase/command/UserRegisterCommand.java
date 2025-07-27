package kr.hhplus.be.server.userservice.application.usecase.command;

import kr.hhplus.be.server.userservice.domain.Address;

public record UserRegisterCommand(
        String name,
        String email,
        String password,
        Address address
) {}

