package kr.hhplus.be.server.userservice.infra.gateway;

import kr.hhplus.be.server.userservice.infra.gateway.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserJpaRepo extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
    Optional<UserEntity> findByEmail(String email);
}
