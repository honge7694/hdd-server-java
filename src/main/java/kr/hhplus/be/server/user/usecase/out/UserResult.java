package kr.hhplus.be.server.user.usecase.out;

import kr.hhplus.be.server.user.domain.Address;

public record UserResult(
        Long id,
        String name,
        String email,
        int balance,
        Address address
) {}
