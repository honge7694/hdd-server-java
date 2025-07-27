package kr.hhplus.be.server.userservice.application.usecase.result;

import kr.hhplus.be.server.userservice.domain.Address;

public record UserResult(
        Long id,
        String name,
        String email,
        int balance,
        Address address
) {}
