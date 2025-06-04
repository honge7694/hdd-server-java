package kr.hhplus.be.server.user.adapter.gateway;

import kr.hhplus.be.server.user.adapter.gateway.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepo extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {

}
