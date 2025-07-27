package kr.hhplus.be.server.userservice.application.interactor;

import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.userservice.application.usecase.port.in.UserFinder;
import kr.hhplus.be.server.userservice.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.userservice.application.usecase.result.UserResult;
import kr.hhplus.be.server.userservice.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserFinderInteractor implements UserFinder {

    private final UserRepository userRepository;

    public UserFinderInteractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResult findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        return new UserResult (user.getId(), user.getName(), user.getEmail(), user.getBalance(), user.getAddress());
    }
}
