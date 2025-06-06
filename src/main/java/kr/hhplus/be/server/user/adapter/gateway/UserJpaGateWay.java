package kr.hhplus.be.server.user.adapter.gateway;


import kr.hhplus.be.server.user.adapter.gateway.entity.UserEntity;
import kr.hhplus.be.server.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserJpaGateWay implements UserRepository {

    private final UserJpaRepo userJpaRepo;

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        UserEntity saved = userJpaRepo.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepo.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findById(Long userId) {
        Optional<User> user = userJpaRepo.findById(userId).map(UserEntity::toDomain);
        return user;
    }

    @Override
    public User update(User user) {
        UserEntity entity = UserEntity.applyDomain(user);
        return userJpaRepo.save(entity).toDomain();
    }

}
