package kr.hhplus.be.server.user.usecase.out;

import kr.hhplus.be.server.user.domain.Address;

public record RegisterUserResult(
        Long id,
        String name,
        String email,
        Address address
) {}
