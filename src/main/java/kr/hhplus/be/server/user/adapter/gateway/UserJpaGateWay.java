package kr.hhplus.be.server.user.adapter.gateway;


import kr.hhplus.be.server.user.adapter.gateway.entity.UserEntity;
import kr.hhplus.be.server.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

}
