package kr.hhplus.be.server.user.application.interactor;

import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.application.usecase.command.UserRegisterCommand;
import kr.hhplus.be.server.user.application.usecase.port.in.UserRegisterInput;
import kr.hhplus.be.server.user.application.usecase.port.out.UserOutput;
import kr.hhplus.be.server.user.application.usecase.result.UserResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegisterInteractor implements UserRegisterInput {

    private final UserRepository userRepository; // 내부 포트

    public UserRegisterInteractor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void registerUser(UserRegisterCommand command, UserOutput presenter) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new ConflictException("이미 사용 중인 이메일입니다.");
        }
        User user = User.create(command.name(), command.email(), command.password(), command.address());
        User saved = userRepository.save(user);
        presenter.ok(new UserResult(
                saved.getId(), saved.getName(), saved.getEmail(),  saved.getBalance(), saved.getAddress()
        ));
    }
}
