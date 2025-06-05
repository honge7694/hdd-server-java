package kr.hhplus.be.server.user.adapter.gateway;

import kr.hhplus.be.server.user.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(String email);
}
